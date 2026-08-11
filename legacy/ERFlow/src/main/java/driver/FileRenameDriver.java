package driver;

import java.io.File;
import java.util.UUID;

/**
 * @deprecated
 * */
public class FileRenameDriver {
	public static final String SAVE_FOLDER = "/Users/helios/Documents/eclipse-workspace/myapp/src/main/webapp/ch15/storage";
	
	public static void main(String[] args) {
		File file = new File(SAVE_FOLDER, "read.txt");
		
		System.out.println(rename(file).toString());
	}
	
	public static File rename(File file) {
		int index = -1;
		String name = file.getName();
		String rename = null;
		String extension = ((index = name.lastIndexOf(".")) != -1) ?
				name.substring(index) : "";
		UUID uuid = UUID.randomUUID();
		
		rename = uuid.toString() + extension;
		
		File renameFile = new File(file.getParent(), rename);
		
		while (renameFile.exists()) {
			uuid = UUID.randomUUID();
			rename = uuid.toString() + extension;
		}
		file.renameTo(renameFile);
		
		return renameFile;
	}
}
