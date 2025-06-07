import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class Servidor
{
   private static int portaServidor = 9871;
   private static byte[] receiveData = new byte[1024];
   private static byte[] sendData = new byte[1024];

   public static String cifra(String mensagem){
      mensagem = mensagem.toLowerCase();
      StringBuilder mensagem_cifrada = new StringBuilder();
      int chave = 3;

      for(char i : mensagem.toCharArray())
      {
         if( Character.isLetter(i))
         {
            char letra_cifrada = (char) ('a' + (i - 'a' + chave) % 26);
            mensagem_cifrada.append(letra_cifrada);
         }
      }

      return mensagem_cifrada.toString();
   }

   public static void main(String args[]) throws Exception
   {
      DatagramSocket serverSocket = new DatagramSocket(portaServidor);//Objeto que envia e recebe pacotes UDP

      while(true) 
      {
         DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

         System.out.println("Aguardando datagrama do cliente....");
         serverSocket.receive(receivePacket);

         System.out.println("RECEIVED: " + new String(receivePacket.getData()));
         InetAddress ipCliente = receivePacket.getAddress();
         int portaCliente = receivePacket.getPort();
         sendData = cifra(new String(receivePacket.getData())).getBytes();

         DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipCliente, portaCliente);
         serverSocket.send(sendPacket);
         System.out.println("Enviado...");
      }
   }
}
