import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

class Cliente
{
   private static String ipServidor = "localhost";
   private static int portaServidor = 9871;

   public static String lerString () throws Exception {
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      return in.readLine();
   }

   public static void main(String argv[]) throws Exception
   {
      //Efetua a primitiva socket e connect, respectivamente.
      Socket socket = new Socket(ipServidor, portaServidor);

      //Efetua a primitiva send
      DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
      saida.writeBytes(lerString() + '\n');


      //Efetua a primitiva receive
      BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      System.out.println("FROM SERVER: " + entrada.readLine());

      //Efetua a primitiva close
      socket.close();
   }
}
