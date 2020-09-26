package searchengine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Web search engine which searches string in html files
 * 
 * @author Group18
 *
 */
public class WebSearchEngine {

	static Hashtable<String, Integer> indexes = new Hashtable<String, Integer>();

	/**
	 * Accepts user input by providing options and calls respective method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Welcome prints
		System.out.println("--Welcome to Web Search Engine--");
		System.out.println("--Created by Group 18--");
		System.out.println("--Toral Patel--");
		System.out.println("--Richa Kansara--");
		System.out.println("--Jay Pandya--");
		System.out.println("--Henil Parikh--");

		String value;
		do {
			// Options available in search engine
			System.out.println("Please select one from below 4 options:::");
			System.out.println("1. Searching");
			System.out.println("2. Spell Checking");
			System.out.println("3. Web Crawler");
			System.out.println("4. QUIT");

			Scanner scan = new Scanner(System.in);
			String selection = scan.nextLine();

			WebSearchEngine engine = new WebSearchEngine();
			// executes respective if else block and if value is incorrect then loops
			// continues
			while (!selection.equalsIgnoreCase("")) {
				if (selection.equals("1")) {
					engine.searchSelected(engine);
					break;
				} else if (selection.equals("2")) {
					engine.spellCheckerSelected(engine);
					break;
				} else if (selection.equals("3")) {
					engine.webCrawler();
					break;
				} else if (selection.equals("4")) {
					System.exit(0);
				} else {
					System.out.println("Incorrect input...Please select from below options...");
					System.out.println("1. Searching");
					System.out.println("2. Spell Checking");
					System.out.println("3. Web Crawler");
					selection = scan.nextLine();

					continue;
				}
			}

			// Asks user if still want to use any feature of search engine
			System.out.println("Do You Want To Continue(Y/N)");
			value = scan.nextLine();

		} while (value.equalsIgnoreCase("Y") || value.equalsIgnoreCase("Yes"));

	}

	/**
	 * Takes url as input and crawls that url and finds other links from that page
	 * and downloads that page
	 */
	private void webCrawler() {

		System.out.println("Please enter URL to crawl:::");
		Scanner scan = new Scanner(System.in);

		// scans user input and stores in variable
		String s = scan.nextLine();
		ArrayList<String> listOfUrl = new ArrayList<String>();

		// Open streams of url and stores page in text format to string
		String input = null;
		try {
			In in = new In(s);
			input = in.readAll().toLowerCase();
		} catch (IllegalArgumentException e) {
			System.out.println("[could not open " + s + "]");
		}

		// regex for finding links from string
		String regexp = "(http|https)://(\\w+\\.)+(edu|com|gov|org)";
		Pattern pattern = Pattern.compile(regexp);

		Matcher matcher = pattern.matcher(input);

		// finds match and stores it in list
		while (matcher.find()) {
			String w = matcher.group();
			if (!listOfUrl.contains(w)) {
				listOfUrl.add(w);
				System.out.println("Downloaded HTML file from URL and stored at path::"
						+ downloadHTMLFileFromUrl(w, String.valueOf(listOfUrl.indexOf(w))));
			}
		}
	}

	/**
	 * this method downloads html file by visiting url and stores it in local
	 * 
	 * @param URL
	 * @param fileName
	 * @return
	 */
	private String downloadHTMLFileFromUrl(String URL, String fileName) {
		{
			String fileNameWithExt = fileName + ".html";
			try {
				// Open URL and reads html page
				URL url2 = new URL(URL);
				BufferedReader reader = new BufferedReader(new InputStreamReader(url2.openStream()));

				BufferedWriter writer = new BufferedWriter(
						new FileWriter("C:\\Users\\patel\\Downloads\\New folder\\" + fileNameWithExt));

				// reads and stores it in file
				String line;
				while ((line = reader.readLine()) != null) {
					writer.write(line);
				}

				reader.close();
				writer.close();
				System.out.println("Successfully Downloaded.");

				return ("C:\\Users\\patel\\Downloads\\New folder\\" + fileNameWithExt);
			} catch (MalformedURLException mue) {
				return "Could Open URL " + URL + " and download file";
			} catch (IOException ie) {
				return "Could Open URL " + URL + " and download file";
			}
		}
	}

	/**
	 * this method accepts html file as input and creates dictionary of words then
	 * takes a word as input and spell checks it by finding it in dictionary
	 * 
	 * @param engine
	 */
	private void spellCheckerSelected(WebSearchEngine engine) {
		System.out.println("Please provide path to HTML file");
		Scanner scan = new Scanner(System.in);
		String filePath = scan.nextLine();

		File sourceFile = new File(filePath);
		File destFile = new File(sourceFile.getAbsolutePath().substring(0, filePath.length() - 4) + ".txt");

		// coverts html to text
		BinarySearchTree<String> t = new BinarySearchTree<>();
		try {
			engine.htmlToTextConvert(sourceFile, destFile);
			String data = engine.textToString(destFile.getAbsolutePath());
			String[] splittedData = data.split("\\s+");
			// adds word to BST if it is not present
			for (String word : splittedData) {
				if (!t.contains(word))
					t.insert(word);
			}
			System.out.println("Created Dictionary with unique words");
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Please enter word to check spelling in dictionary:::");
		String searchString = scan.nextLine();

		// Checks word search is present in dictionary or not
		if (t.contains(searchString)) {
			System.out.println("Correctly Spelled");
		} else {
			System.out.println("Incorrectly Spelled");
		}
	}

	/**
	 * Accepts search string as input and finds occurrences of words in 101 html
	 * files and displays count in descending order
	 * 
	 * @param engine
	 */
	private void searchSelected(WebSearchEngine engine) {
		System.out.println("Please enter your search string:::");

		Scanner scan = new Scanner(System.in);
		String searchString = scan.nextLine();

		engine.listHTMLFiles();

		System.out.println("Searching Web...");

		// Iterates directory of html files
		File folder = new File("C:\\Users\\patel\\Downloads\\W3C Web Pages\\Text");
		indexes.clear();
		for (final File fileEntry : folder.listFiles()) {

			engine.searchUsingBFM(fileEntry.getAbsolutePath(), fileEntry.getName(), searchString);
		}

		// Sorting map in reverse order
		Map<String, Integer> sortedByCount = indexes.entrySet().stream()
				.sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		System.out.println("Ranking Pages in descending order of its occurrence sount...");

		sortedByCount.forEach((k, v) -> System.out.println("File Name : " + k + " Count : " + v));

		// If search word is not found in any of above pages then it will look for
		// minimum edit distance and prints top 10
		if (indexes.isEmpty()) {
			System.out.println("Could not find exact match...");
			System.out.println("Finding similar word with minimum edit distance...");
			engine.findWordWithRegex(searchString);
			if (indexes.size() > 0) {
				System.out.println("Showing Top 10 pages with minimum edit distance...");

				// map sorted with minimum edit distance with limit 10
				sortedByCount = indexes.entrySet().stream().sorted(Map.Entry.comparingByValue()).limit(10).collect(
						Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

				sortedByCount.forEach((k, v) -> System.out.println("Word : " + k + " Distance : " + v));
			} else {
				System.out.println("Searh Not Found......Try Again...");
			}
		}
	}

	/**
	 * Accepts source and destination path and converts it to text file
	 * 
	 * @param sourceHtmlFile
	 * @param destinationTextFile
	 * @throws IOException
	 */
	private void htmlToTextConvert(File sourceHtmlFile, File destinationTextFile) throws IOException {
		Document document = Jsoup.parse(sourceHtmlFile, "UTF-8");
		String docContent = document.text();
		FileWriter fw = new FileWriter(destinationTextFile);
		fw.write(docContent);
		fw.close();
	}

	/**
	 * iterates directory with html files to convert it to text
	 */
	private void listHTMLFiles() {

		File directory = new File("C:\\Users\\patel\\Downloads\\W3C Web Pages");
		File[] listOfFiles = directory.listFiles();
		File textFileDirectory = new File(directory.getAbsolutePath() + "\\Text");
		if (!textFileDirectory.exists()) {
			textFileDirectory.mkdir();
		}
		for (int i = 0; i < listOfFiles.length; i++) {

			String textFileName = textFileDirectory + "\\" + listOfFiles[i].getName();
			textFileName = textFileName.substring(0, textFileName.length() - 4);
			textFileName += ".txt";

			if (listOfFiles[i].isFile()) {
				try {
					htmlToTextConvert(listOfFiles[i], new File(textFileName));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * Uses Brute Force Match algorithn to search word in text file
	 * 
	 * @param textFilePath
	 * @param fileName
	 * @param pattern
	 */
	private void searchUsingBFM(String textFilePath, String fileName, String pattern) {
		String txt = textToString(textFilePath);
		int pre_index = 0;
		int count = 0;
		while (BruteForceMatch.search1(pattern, txt) != txt.length()) {
			int index = BruteForceMatch.search1(pattern, txt);
			pre_index = index + pre_index;
			indexes.put(fileName, ++count);
			if (txt.length() > index + pattern.length())
				txt = txt.substring(index + pattern.length());
		}
	}

	/**
	 * This method reads text file and returns string
	 * 
	 * @param textFilePath
	 * @return
	 */
	private String textToString(String textFilePath) {
		StringBuilder contentBuilder = new StringBuilder();
		try (FileReader reader = new FileReader(textFilePath); BufferedReader br = new BufferedReader(reader)) {
			// read line by line
			String line;
			while ((line = br.readLine()) != null) {
				contentBuilder.append(line).append(" ");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contentBuilder.toString();
	}

	/**
	 * Uses regex to find word from text file calculates edit difference with search
	 * word
	 * 
	 * @param searchString
	 */
	private void findWordWithRegex(String searchString) {
		String regex = "[A-Za-z0-9]+";
		String data = " ";

		ArrayList<String> arrayList = new ArrayList<String>();

		Pattern r3 = Pattern.compile(regex);
		Matcher m3 = r3.matcher(data);
		indexes.clear();
		File folder = new File("C:\\Users\\patel\\Downloads\\W3C Web Pages\\Text");
		for (final File fileEntry : folder.listFiles()) {
			try (FileReader reader = new FileReader(fileEntry.getAbsolutePath());
					BufferedReader br = new BufferedReader(reader)) {
				// read line by line
				String line;
				while ((line = br.readLine()) != null) {
					m3.reset(line);
					while (m3.find()) {
						arrayList.add(m3.group());
					}
				}
				for (int p = 0; p < arrayList.size(); p++) {
					// calculates edit distance
					int distance = Sequences.editDistance(searchString.toLowerCase(), arrayList.get(p).toLowerCase());
					if (indexes.contains(fileEntry.getName()))
						// if distance is smaller than it gets replaced in HashtABLE
						indexes.put(fileEntry.getName(),
								indexes.get(fileEntry.getName()) < distance ? indexes.get(fileEntry.getName())
										: distance);
					else
						indexes.put(fileEntry.getName(), distance);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
