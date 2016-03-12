package pl.agh.distributedSystems.lab1.sendingFile.message;

import lombok.Data;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@Data
public class FileToSend {
	private String filename;
    private byte[] file;

    public FileToSend(){
    }
	public FileToSend(File file) throws IOException {
        this.filename = file.getName();
        this.file = FileUtils.readFileToByteArray(file);
    }
    public FileToSend(String filename, byte[] file){
        this.file=file;
        this.filename=filename;
    }
}
