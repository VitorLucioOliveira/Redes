import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

class Servidor
{
   private static int portaServidor = 9871;

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

   public static void main(String argv[]) throws Exception
   {
      try (//Efetua as primitivas socket e bind, respectivamente
      ServerSocket socket = new ServerSocket(portaServidor)) {
         while(true)
         {
            //Efetua as primitivas de listen e accept, respectivamente
            Socket conexao = socket.accept();

            //Efetua a primitiva receive
            System.out.println("Aguardando datagrama do cliente....");
            BufferedReader entrada =  new BufferedReader(new InputStreamReader(conexao.getInputStream()));

            //Operacao com os dados recebidos e preparacao dos a serem enviados
            String str = entrada.readLine();
            System.out.println("Received: " + str);

            str = cifra(str) + '\n';

            //Efetua a primitiva send
            DataOutputStream saida = new DataOutputStream(conexao.getOutputStream());
            saida.writeBytes(str);

            conexao.close();
         }
      }
   }
}
