package logic.sincronia;
// tiene los metodos para comunicacion saliente

import java.nio.ByteBuffer;
import logic.interfaz.Adsobalin;

public abstract class Envios {
    
    // listado de tipos de mensajes
    public static final byte MSJ_HOLA = 0;
    public static final byte MSJ_MSJ = 1;
    
    // listado de tipos de submensajes
    public static final byte SUB_VERSION = 0;
    public static final byte SUB_CUPO = 1;
    public static final byte SUB_ENCURSO = 2;
    public static final byte SUB_ESTILO = 3;
    public static final byte SUB_NOMBRE = 4;
    
    public static boolean sendHola(String nombre, String destino,
            int estilo, int grupo) {
        // crear un buffer para armar el mensaje
        ByteBuffer buff = Conector.newBuffer(MSJ_HOLA,
            2 + Short.BYTES + nombre.length() + 1);
        
        // ingresar los datos especificos
        buff.putShort((short)Adsobalin.VERSION);
        buff.put((byte)estilo);
        buff.put((byte)grupo);
        Conector.buffPutString(buff, nombre);
        
        // empaquetar el buffer y enviarlo
        return Conector.enviaMsj(Conector.buf2arr(buff), destino);
    }
    
    public static boolean sendMsj(byte submsj, String destino) {
        // crear un buffer para armar el mensaje y ponerle el dato
        ByteBuffer buff = Conector.newBuffer(MSJ_MSJ, 1);
        buff.put(submsj);
        
        // empaquetar el buffer y enviarlo
        return Conector.enviaMsj(Conector.buf2arr(buff), destino);
    }
}
