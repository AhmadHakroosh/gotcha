package gotcha.controller.search;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import gotcha.controller.search.model.GotchaQuery;
import gotcha.globals.Globals;
import gotcha.model.Channel;
import gotcha.model.Message;
import gotcha.model.User;

public class GotchaSearchEngine {
	
	private Analyzer analyzer;
	private Similarity similarity;
	private IndexWriterConfig config;
	private Directory directory;
	private IndexWriter indexWriter;
	private IndexReader indexReader;
	private QueryParser parser;
	private DirectoryReader directoryReader;
	private IndexSearcher indexSearcher;
	
	private static GotchaSearchEngine engine;
	
	private GotchaSearchEngine () {
		// Instantiate the search engine by indexing the whole database
		try {
			analyzer = new EnglishAnalyzer();
			similarity = new BM25Similarity();
			config = new IndexWriterConfig(analyzer);
			config.setSimilarity(similarity);
			File indexDirectory = new File("gotchaIndex");
			directory = FSDirectory.open(indexDirectory.toPath());
			config.setOpenMode(OpenMode.CREATE_OR_APPEND);
			indexWriter = new IndexWriter(directory, config);
			indexDatabase();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// Get an instance of the search engine
	public static GotchaSearchEngine create () {
		if (engine == null) {
			engine = new GotchaSearchEngine();
		}
				
		return engine;
	}
	// Perform database indexing
	private void indexDatabase () {
		ResultSet resultSet;
		ArrayList<Object> values = new ArrayList<Object>();
		ArrayList<Object> where = new ArrayList<Object>();
		// Index all system users
		resultSet = Globals.execute(Globals.SELECT_ALL_USERS, values, where);
		
		try {
			while (resultSet.next()) {
				Document document = new Document();
				document.add(new StringField("id", resultSet.getString("USERNAME"), Field.Store.YES));
				document.add(new StringField("type", "User", Field.Store.YES));
				document.add(new StringField("username", resultSet.getString("USERNAME"), Field.Store.YES));
				document.add(new StringField("nickname", resultSet.getString("NICKNAME"), Field.Store.YES));
				document.add(new TextField("description", resultSet.getString("DESCRIPTION"), Field.Store.YES));
				indexWriter.addDocument(document);
			}
			// Index all system channels
			resultSet = Globals.execute(Globals.SELECT_ALL_CHANNELS, values, where);
			while (resultSet.next()) {
				Document document = new Document();
				document.add(new StringField("id", resultSet.getString("NAME"), Field.Store.YES));
				document.add(new StringField("type", "Channel", Field.Store.YES));
				document.add(new StringField("name", resultSet.getString("NAME"), Field.Store.YES));
				document.add(new TextField("description", resultSet.getString("DESCRIPTION"), Field.Store.YES));
				indexWriter.addDocument(document);
			}
			// Index all system messages
			resultSet = Globals.execute(Globals.SELECT_ALL_MESSAGES, values, where);
			while (resultSet.next()) {
				Document document = new Document();
				document.add(new StringField("id", resultSet.getString("ID"), Field.Store.YES));
				document.add(new StringField("type", "Message", Field.Store.YES));
				document.add(new StringField("from", resultSet.getString("SENDER"), Field.Store.YES));
				document.add(new StringField("to", resultSet.getString("RECEIVER"), Field.Store.YES));
				document.add(new TextField("text", resultSet.getString("TEXT"), Field.Store.YES));
				document.add(new StringField("time", (resultSet.getTimestamp("SENT_TIME")).toString(), Field.Store.YES));
				indexWriter.addDocument(document);
			}
			
			indexWriter.commit();
			indexWriter.close();
			
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Object> search (GotchaQuery gotchaQuery) {
		
		ArrayList<Object> found = new ArrayList<Object>();
		
		try {
			parser = new QueryParser(gotchaQuery.parse(), analyzer);
			Query query = parser.parse(QueryParser.escape(gotchaQuery.what()));
			directoryReader = DirectoryReader.open(directory);
			indexSearcher = new IndexSearcher(directoryReader);
			indexSearcher.setSimilarity(similarity);
			TopDocs result = indexSearcher.search(query, 10);
			ScoreDoc[] foundDocuments = result.scoreDocs;
			indexReader = indexSearcher.getIndexReader();
			
			for (ScoreDoc one : foundDocuments) {
				Document document = indexReader.document(one.doc);
				
				switch (document.get("type")) {
					
					case "User":
						User user = new User();
						user.username(document.get("username"));
						user.nickName(document.get("nickname"));
						user.description(document.get("description"));
						found.add(user);
						break;
						
					case "Channel":
						Channel channel = new Channel();
						channel.name(document.get("name"));
						channel.description(document.get("description"));
						found.add(channel);
						break;
						
					case "Message":
						Message message = new Message();
						message.from(document.get("from"));
						message.to(document.get("to"));
						message.text(document.get("text"));
						// Format the date string of the message
						DateFormat format = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
						Date time = format.parse(document.get("time"));
						Timestamp timestamp = new Timestamp(time.getTime());
						message.time(timestamp);
						found.add(message);
						break;
				}
			}
			
		} catch (ParseException | IOException | java.text.ParseException e) {
			System.out.println("An unknown error has occured while trying to parse the query.");
		}
		
		return found;
	}
	
	public void add (Object object) {
		
	}
	
	public void remove (Object object) {
		
	}
}
