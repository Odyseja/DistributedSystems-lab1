package pl.agh.distributedSystems.lab1.sendingFile.message;

import lombok.Data;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@Data
public class Message {
	private String filename;
    private byte[] file;

    public Message(){
    }
	public Message(File file) throws IOException {
        this.filename = file.getName();
        this.file = FileUtils.readFileToByteArray(file);
    }
    public Message(String filename, byte[] file){
        this.file=file;
        this.filename=filename;
    }
}
