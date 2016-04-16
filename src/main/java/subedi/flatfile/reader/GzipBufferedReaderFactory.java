/**
 * 
 */
package subedi.flatfile.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

import org.springframework.batch.item.file.BufferedReaderFactory;
import org.springframework.core.io.Resource;

import subedi.flatfile.util.FlatFileUtil;

/**
 * @author vivek.subedi
 *
 */
public class GzipBufferedReaderFactory implements BufferedReaderFactory {
	
	private static final int BYTE_READ_SIZE = 65536;
	
	public BufferedReader create(File file, String encoding) throws UnsupportedEncodingException, IOException {
		BufferedReader bufferedReader = null;		
		GZIPInputStream gzipInputStream = null;
		InputStreamReader streamReader = null;
		FileInputStream fileInputStream = new FileInputStream(file);

		if (file.getName().endsWith(FlatFileUtil.GZIP_FILE_END_SUFFIX)) {
			try {		
				gzipInputStream = new GZIPInputStream(fileInputStream, BYTE_READ_SIZE);
				streamReader = new InputStreamReader(gzipInputStream, encoding);
				bufferedReader = new BufferedReader(streamReader, BYTE_READ_SIZE);
			} catch (ZipException zipException) {
				throw new IllegalStateException("File ["+file.getName()+"] can't be read in provided gz format.", zipException);
			}		
		} else {
			bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, encoding), BYTE_READ_SIZE);
		}

		return bufferedReader;		
	}
	
	@Override
	public BufferedReader create(Resource resource, String encoding) throws UnsupportedEncodingException, IOException {
		return this.create(resource.getFile(), encoding);
	}

}
