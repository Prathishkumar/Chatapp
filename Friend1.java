import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Friend1 extends Frame implements Runnable, ActionListener {

    TextField textField;
    TextArea textArea;
    Button send;
    ServerSocket serverSocket;
    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    Thread chat;

    Friend1() {
        
        textField = new TextField(40); 
        textArea = new TextArea(20, 50); 
        textArea.setEditable(false); 
        send = new Button("Send");

        send.addActionListener(this);

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    actionPerformed(new ActionEvent(send, ActionEvent.ACTION_PERFORMED, "Send"));
                }
            }
        });

        try {
            serverSocket = new ServerSocket(12000);
            System.out.println("Waiting for a friend to connect...");
            socket = serverSocket.accept();
            System.out.println("Friend connected.");

            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        chat = new Thread(this);
        chat.setDaemon(true);
        chat.start();

        setLayout(new FlowLayout());
        add(textArea);
        add(textField);
        add(send);

        setSize(600, 400);
        setTitle("Friend1 Chat");
        setVisible(true);

        // Window close listener
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    if (dataInputStream != null) dataInputStream.close();
                    if (dataOutputStream != null) dataOutputStream.close();
                    if (socket != null) socket.close();
                    if (serverSocket != null) serverSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                System.exit(0);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = textField.getText().trim();
        if (!msg.isEmpty()) {
            textArea.append("Friend1: " + msg + "\n");
            textField.setText("");
            try {
                dataOutputStream.writeUTF(msg);
                dataOutputStream.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (dataInputStream != null) {
                    String msg = dataInputStream.readUTF();
                    textArea.append("Friend2: " + msg + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void main(String[] args) {
        new Friend1();
    }
}
