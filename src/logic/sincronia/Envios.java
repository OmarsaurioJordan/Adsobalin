package logic.sincronia;
// tiene los metodos para comunicacion saliente

import java.nio.ByteBuffer;
import logic.interfaz.Adsobalin;

public abstract class Envios {
    
    // listado de tipos de mensajes
    public static byte msjHOLA = 0;
    
    public static boolean sendHola(String nombre, String destino,
            int estilo, int grupo) {
        // crear un buffer para armar el mensaje
        ByteBuffer buff = Conector.newBuffer(msjHOLA,
            2 + Short.BYTES + nombre.length() + 1);
        
        // ingresar los datos especificos
        buff.putShort((short)Adsobalin.version);
        buff.put((byte)estilo);
        buff.put((byte)grupo);
        Conector.buffPutString(buff, nombre);
        
        // empaquetar el buffer y enviarlo
        return Conector.enviaMsj(Conector.buf2arr(buff), destino);
    }
}
