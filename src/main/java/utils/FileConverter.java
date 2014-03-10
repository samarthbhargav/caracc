package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

public class FileConverter {
	
	/*
	 * buying: vhigh, high, med, low.
		maint: vhigh, high, med, low.
		doors: 2, 3, 4, 5more.
		persons: 2, 4, more.
		lug_boot: small, med, big.
		safety: low, med, high. 
	 */
	
	static String[] labelIndex = {"buying_", "maint_", "doors_", "persons_", "lug_boot_", "safety_" };
	
	public static String convertToCanonical(long key, String s) {
		StringBuilder res = new StringBuilder();
		
		String[] arr = s.trim().split(",");
		
		res.append(arr[arr.length - 1] + "\t");
		res.append(key + "\t");
		for(int i =0 ; i < arr.length - 1; i++) {
			res.append(labelIndex[i] + arr[i] +" ");
		}
		res.append("\n");
		
		return res.toString();
	}
	
	
	public static void convertToTSVFile(String fname, String dest) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fname));
		BufferedWriter bw = new BufferedWriter(new FileWriter(dest));
		
		String line = null;
		long key = 0;
		while(( line = br.readLine() ) != null ) {
			if(line.trim().length() == 0) {
				continue;
			}
			
			bw.write(convertToCanonical(key++, line));
		}
		
		
		br.close();
		bw.close();
	}
	
	
	public static void convertToSequenceFile(String src, String dest) throws IOException {
		BufferedReader br = new BufferedReader( new FileReader( src ) );
		
		Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get( conf );
        System.out.println(fs.getHomeDirectory());
        Path outputPath = new Path( dest );
        System.out.println( conf );
        System.out.println( fs );

        LongWritable key = new LongWritable();
        SequenceFile.Writer writer = new SequenceFile.Writer( fs, conf, outputPath, Text.class, Text.class );


        String line = null;

        key.set( 0 );
        while ( ( line = br.readLine() ) != null ) {
            String[] arr = line.split( "\t" );
            
            if(arr.length != 3) {
            	continue;
            }
            
            Text k = new Text();
            Text v = new Text();
            
            k.set("/" + arr[0] + "/" + arr[1]);
            v.set(arr[2]);
            
            writer.append(k, v);
        }
        
        writer.close();
        br.close();
    }
	
	public static void main(String[] args) throws IOException {
		convertToTSVFile("car.data", "car.tsv");
		convertToSequenceFile("car.tsv", "car.seq");
	}	
}