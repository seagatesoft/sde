package org.seagatesoft.sde;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.List;
import java.util.ArrayList;

import org.seagatesoft.sde.columnaligner.ColumnAligner;
import org.seagatesoft.sde.columnaligner.PartialTreeAligner;
import org.seagatesoft.sde.datarecordsfinder.DataRecordsFinder;
import org.seagatesoft.sde.datarecordsfinder.MiningDataRecords;
import org.seagatesoft.sde.dataregionsfinder.DataRegionsFinder;
import org.seagatesoft.sde.dataregionsfinder.MiningDataRegions;
import org.seagatesoft.sde.tagtreebuilder.DOMParserTagTreeBuilder;
import org.seagatesoft.sde.tagtreebuilder.TagTreeBuilder;
import org.seagatesoft.sde.treematcher.EnhancedSimpleTreeMatching;
import org.seagatesoft.sde.treematcher.SimpleTreeMatching;
import org.seagatesoft.sde.treematcher.TreeMatcher;
import org.xml.sax.SAXException;

/**
 * Aplikasi utama yang berbasis konsol.
 * 
 * @author seagate
 *
 */
public class AppConsole
{
	/*
	 * Formatter untuk menulis ke file output
	 */
	public static Formatter output;

	/**
	 * Method main untuk aplikasi utama yang berbasis konsol. Ada empat argumen yang bisa diberikan, 
	 * urutannya URI file input, URI file output, similarity treshold, jumlah node maksimum dalam generalized node.
	 *  
	 * @param args Parameter yang dimasukkan pengguna aplikasi konsol
	 */
	public static void main(String args[])
	{
		// parameter default
		String input = "";
		String resultOutput = "MDR.html";
		double similarityTreshold = 0.80;
		boolean ignoreFormattingTags = false;
		boolean useContentSimilarity = false;
		int maxNodeInGeneralizedNodes = 9;
		
		// parameter dari pengguna, urutannya URI file input, URI file output, similarity treshold, jumlah node maksimum dalam generalized node
		// parameter yang wajib adalah parameter URI file input
		switch (args.length)
		{
			case 0:
				System.err.println("Please specify the extraction URI...");
				System.exit(1);
			case 1:
				input = args[0];
				break;
			case 2:
				input = args[0];
				resultOutput = args[1];
				break;
			case 3:
				input = args[0];
				resultOutput = args[1];
				similarityTreshold = Double.parseDouble( args[2] );
				break;
			case 4:
				input = args[0];
				resultOutput = args[1];
				similarityTreshold = Double.parseDouble( args[2] );
				ignoreFormattingTags = Boolean.parseBoolean( args[3] );
				break;
			case 5:
				input = args[0];
				resultOutput = args[1];
				similarityTreshold = Double.parseDouble( args[2] );
				ignoreFormattingTags = Boolean.parseBoolean( args[3] );
				useContentSimilarity = Boolean.parseBoolean( args[4] );
				break;
			case 6:
				input = args[0];
				resultOutput = args[1];
				similarityTreshold = Double.parseDouble( args[2] );
				ignoreFormattingTags = Boolean.parseBoolean( args[3] );
				useContentSimilarity = Boolean.parseBoolean( args[4] );
				maxNodeInGeneralizedNodes = Integer.parseInt( args[5] );
				break;
		}

		try
		{
			// siapkan file output
			output = new Formatter(resultOutput);
			// buat objek TagTreeBuilder yang berbasis parser DOM
			TagTreeBuilder builder = new DOMParserTagTreeBuilder();
			// bangun pohon tag dari file input menggunakan objek TagTreeBuilder yang telah dibuat
			TagTree tagTree = builder.buildTagTree(input, ignoreFormattingTags);
			//print(A.getRoot(), " ");
			//printHTML( A.getRoot());
			// buat objek TreeMatcher yang menggunakan algoritma simple tree matching
			TreeMatcher matcher = new SimpleTreeMatching();
			// buat objek DataRegionsFinder yang menggunakan algoritma mining data regions dan
			// menggunakan algoritma pencocokan pohon yang telah dibuat sebelumnya
			DataRegionsFinder dataRegionsFinder = new MiningDataRegions( matcher );
			// cari data region pada pohon tag menggunakan objek DataRegionsFinder yang telah dibuat
			List<DataRegion> dataRegions = dataRegionsFinder.findDataRegions(tagTree.getRoot(), maxNodeInGeneralizedNodes, similarityTreshold);
			// buat objek DataRecordsFinder yang menggunakan metode mining data records dan
			// menggunakan algoritma pencocokan pohon yang telah dibuat sebelumnya
			DataRecordsFinder dataRecordsFinder = new MiningDataRecords( matcher );
			// buat matriks DataRecord untuk menyimpan data record yang teridentifikasi oleh 
			// DataRecordsFinder dari List<DataRegion> yang ditemukan
			DataRecord[][] dataRecords = new DataRecord[ dataRegions.size() ][];
			
			// identifikasi data records untuk tiap2 data region 
			for( int dataRecordArrayCounter = 0; dataRecordArrayCounter < dataRegions.size(); dataRecordArrayCounter++)
			{
				DataRegion dataRegion = dataRegions.get( dataRecordArrayCounter );
				dataRecords[ dataRecordArrayCounter ] = dataRecordsFinder.findDataRecords(dataRegion, similarityTreshold);
			}
			
			// buat objek ColumnAligner yang menggunakan algoritma partial tree alignment
			ColumnAligner aligner = null;
			if ( useContentSimilarity )
			{
				aligner = new PartialTreeAligner( new EnhancedSimpleTreeMatching() );
			}
			else
			{
				aligner = new PartialTreeAligner( matcher );
			}
			List<String[][]> dataTables = new ArrayList<String[][]>();

			// bagi tiap2 data records ke dalam kolom sehingga berbentuk tabel
			// dan buang tabel yang null
			for(int tableCounter=0; tableCounter< dataRecords.length; tableCounter++)
			{
				String[][] dataTable = aligner.alignDataRecords( dataRecords[tableCounter] );

				if ( dataTable != null )
				{
					dataTables.add( dataTable );
				}
			}
			
			int recordsFound = 0;
						
			for ( String[][] dataTable: dataTables )
			{
				recordsFound += dataTable.length;
			}			

			// write extracted data to output file
			output.format("<html><head><title>Extraction Result</title>");
			output.format("<style type=\"text/css\">table {border-collapse: collapse;} td {padding: 5px} table, th, td { border: 3px solid black;} </style>");
			output.format("</head><body>");
			int tableCounter = 1;
			for ( String[][] table: dataTables)
			{
				output.format("<h2>Table %s</h2>\n<table>\n<thead>\n<tr>\n<th>Row Number</th>\n", tableCounter);
				for(int columnCounter=1; columnCounter<=table[0].length; columnCounter++)
				{
					output.format("<th></th>\n");
				}
				output.format("</tr>\n</thead>\n<tbody>\n");
				int rowCounter = 1;
				for (String[] row: table)
				{
					output.format("<tr>\n<td>%s</td>", rowCounter);
					int columnCounter = 1;
					for(String item: row)
					{
						String itemText = item;
						if (itemText == null)
						{
							itemText = "";
						}
						output.format("<td>%s</td>\n", itemText);
						columnCounter++;
					}
					output.format("</tr>\n");
					rowCounter++;
				}
				output.format("</tbody>\n</table>\n");
				tableCounter++;
			}
			
			output.format("</body></html>");
		}
		catch (SecurityException exception)
		{
			exception.printStackTrace();
			System.exit(1);
		}
		catch(FileNotFoundException exception)
		{
			exception.printStackTrace();
			System.exit(2);
		}
		catch(IOException exception)
		{
			exception.printStackTrace();
			System.exit(3);
		}
		catch(SAXException exception)
		{
			exception.printStackTrace();
			System.exit(4);
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
			System.exit(5);
		}
		finally
		{
			if ( output != null )
			{
				output.close();
			}
		}
	}
	
	public static void printHTML(TagNode tagNode)
	{
		if (tagNode.getInnerText() == null)
		{
			output.format("<%s>", tagNode);
		}
		else
		{
			output.format("<%s>%s", tagNode, tagNode.getInnerText());
		}
				
		for ( TagNode child: tagNode.getChildren() )
		{
			printHTML(child);
			child = child.getNextSibling();
		}
		
		output.format("</%s>\n", tagNode);
	}
	
	public static void printTree(TagNode tagNode, String indent)
	{
		output.format("%s%s<br />", indent, tagNode.toString());
		output.format("%s%s<br />", indent, tagNode.getInnerText());
		
		for (TagNode child: tagNode.getChildren() )
		{
			printTree( child, "&nbsp;#&nbsp;"+indent);
		}
	}
}