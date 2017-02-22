package gotcha.controller.search;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
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
import gotcha.model.User;

/**
 * Search engine, indexes the users names and channels.
 */
public class GotchaSearchEngine {
	
	private Analyzer analyzer;
	private Similarity similarity;
	private IndexWriterConfig config;
	private Directory directory;
	private IndexWriter indexWriter;
	private QueryParser parser;
	private IndexReader indexReader;
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
			config.setOpenMode(OpenMode.CREATE);
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
		PreparedStatement statement;
		ResultSet resultSet;
		
		try {
			// Index all system users
			Connection connection = Globals.database.getConnection();
			statement = connection.prepareStatement(Globals.SELECT_ALL_USERS);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				Document document = new Document();
				document.add(new TextField("type", "User", Field.Store.YES));
				document.add(new TextField("name", resultSet.getString("NICKNAME"), Field.Store.YES));
				indexWriter.addDocument(document);
			}
			// Index all system channels
			statement = connection.prepareStatement(Globals.SELECT_ALL_CHANNELS);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				Document document = new Document();
				document.add(new TextField("type", "Channel", Field.Store.YES));
				document.add(new TextField("name", resultSet.getString("NAME"), Field.Store.YES));
				indexWriter.addDocument(document);
			}
			
			statement.close();
			connection.close();
			indexWriter.commit();
			indexWriter.close();
			
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public HashMap<String, Object> search (GotchaQuery gotchaQuery) {
		
		HashMap<String, Object> found = new HashMap<String ,Object>();
		PreparedStatement statement;
		ResultSet resultSet;
		
		try {
			parser = new QueryParser("name", analyzer);
			Query query = parser.parse(QueryParser.escape(gotchaQuery.what()));
			directoryReader = DirectoryReader.open(directory);
			indexSearcher = new IndexSearcher(directoryReader);
			indexSearcher.setSimilarity(similarity);
			TopDocs result = indexSearcher.search(query, 100);
			ScoreDoc[] foundDocuments = result.scoreDocs;
			indexReader = indexSearcher.getIndexReader();

			Connection connection = Globals.database.getConnection();
			
			for (ScoreDoc one : foundDocuments) {
				Document document = indexReader.document(one.doc);
				
				if (document.get("type").equals("User")) {
					// Get the user profile and all the channels he's subscribed to
					statement = connection.prepareStatement(Globals.SELECT_USER_BY_NICKNAME);
					statement.setString(1, document.get("name"));
					resultSet = statement.executeQuery();
					try {
						while (resultSet.next()) {
							User user = new User();
							user.photoUrl(resultSet.getString("PHOTO_URL"));
							user.nickName(resultSet.getString("NICKNAME"));
							user.description(resultSet.getString("DESCRIPTION"));
							user.status(resultSet.getString("STATUS"));
							found.put(user.nickName(), user);
							// Add the channels that the user is subscribed to
							ArrayList<Channel> channels = Globals.getUserSubscriptions(user.nickName());
							for (Channel channel : channels) {
								if (!found.containsKey(channel.name())) {
									found.put(channel.name(), channel);
								}
							}
						}					
					} catch (SQLException e) {
						System.out.println("An error has occured while trying to retrieve user data from database.");
					}
					
					statement.close();
					
				} else {
					// Get the channel details
					statement = connection.prepareStatement(Globals.SELECT_CHANNEL_BY_NAME);
					statement.setString(1, document.get("name"));
					resultSet = statement.executeQuery();
					try {
						while (resultSet.next()) {
							Channel channel = new Channel();
							channel.name(resultSet.getString("NAME"));
							channel.description(resultSet.getString("DESCRIPTION"));
							// Check whether the channel has already been found once before
							if (!found.containsKey(channel.name())) {
								found.put(channel.name(), channel);
							}
						}
					} catch (SQLException e) {
						System.out.println("An error has occured while trying to retrieve channel data from database.");
					}

					statement.close();
				}
			}
			
			connection.close();
			
		} catch (ParseException | IOException | SQLException e) {
			System.out.println("An unknown error has occured while trying to parse the query.");
		}
		
		return found;
	}
	
	public void add (Object object) {
		try {
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			config.setSimilarity(similarity);
			config.setOpenMode(OpenMode.CREATE_OR_APPEND);
			indexWriter = new IndexWriter(directory, config);
			
			if (object instanceof User) {
				User user = (User) object;
				Document document = new Document();
				document.add(new TextField("type", "User", Field.Store.YES));
				document.add(new TextField("name", user.nickName(), Field.Store.YES));
				indexWriter.addDocument(document);
				
			} else {
				Channel channel = (Channel) object;
				Document document = new Document();
				document.add(new TextField("type", "Channel", Field.Store.YES));
				document.add(new TextField("name", channel.name(), Field.Store.YES));
				indexWriter.addDocument(document);
			}
			
			indexWriter.commit();
			indexWriter.close();
			
		} catch (IOException e) {
			System.out.println("An unknown error has occurred while trying to open gotchaIndex!");
		}
	}
	
	public void remove (Object object) {
		try {
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			config.setSimilarity(similarity);
			config.setOpenMode(OpenMode.CREATE_OR_APPEND);
			indexWriter = new IndexWriter(directory, config);
			
			if (object instanceof User) {
				User user = (User) object;
				String [] fields = {"type", "name"};
				String [] queries = {"User", user.nickName()};
				Query query = MultiFieldQueryParser.parse(queries, fields, analyzer);
				indexWriter.deleteDocuments(query);
				
			} else {
				Channel channel = (Channel) object;
				String [] fields = {"type", "name"};
				String [] queries = {"Channel", channel.name()};
				Query query = MultiFieldQueryParser.parse(queries, fields, analyzer);
				indexWriter.deleteDocuments(query);
			}
			
			indexWriter.commit();
			indexWriter.close();
			
		} catch (IOException e) {
			System.out.println("An unknown error has occurred while trying to open gotchaIndex!");
		} catch (ParseException e) {
			System.out.println("An unknown error has occurred while trying to parse the query!");
		}
	}
}
