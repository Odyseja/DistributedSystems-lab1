package pl.agh.distributedSystems.lab1.chat.message;

import lombok.Data;

@Data
public class Message {
	private Long checksum;
	private String nick;
	private String data;
	private String date;
    private byte[] datagram;

    public Message(){
    }
	public Message(String nick, String data){
        this.nick = nick;
        this.data = data;
	}

    public Message(String nick, String data, String date, long checksum){
        this(nick, data);
        this.date=date;
        this.checksum=checksum;
    }
}
