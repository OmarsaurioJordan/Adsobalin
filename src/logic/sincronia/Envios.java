package logic.sincronia;
// tiene los metodos para comunicacion saliente

import java.nio.ByteBuffer;
import logic.interfaz.Adsobalin;

public abstract class Envios {
    
    // listado de tipos de mensajes / quien lo envia
    public static final byte MSJ_HOLA = 0; // C
    public static final byte MSJ_MSJ = 1; // S
    public static final byte MSJ_WELCOME = 2; // S
    public static final byte MSJ_PLAYER = 3; // X
    public static final byte MSJ_DISPARO = 4; // X
    public static final byte MSJ_GOLPE = 5; // X
    public static final byte MSJ_NPC = 6; // S
    public static final byte MSJ_MUNDO = 7; // S
    public static final byte MSJ_LOBBY = 8; // S
    public static final byte MSJ_RESULT = 9; // S
    public static final byte MSJ_PLANO = 10; // C
    
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
    
    public static boolean sendWelcome(String nombre, String destino,
            int estilo, int grupo, int ind) {
        // crear un buffer para armar el mensaje
        ByteBuffer buff = Conector.newBuffer(MSJ_WELCOME,
            3 + nombre.length() + 1);
        
        // ingresar los datos especificos
        buff.put((byte)ind);
        buff.put((byte)estilo);
        buff.put((byte)grupo);
        Conector.buffPutString(buff, nombre);
        
        // empaquetar el buffer y enviarlo
        return Conector.enviaMsj(Conector.buf2arr(buff), destino);
    }
    
    public static boolean sendMsj(byte submsj, String destino) {
        // crear un buffer para armar el mensaje y ponerle el dato
        ByteBuffer buff = Conector.newBuffer(MSJ_MSJ, 1);
        
        // ingresar los datos especificos
        buff.put(submsj);
        
        // empaquetar el buffer y enviarlo
        return Conector.enviaMsj(Conector.buf2arr(buff), destino);
    }
    
    public static boolean sendGolpe(int golpeador, int victima,
            int indDisparo, String destino) {
        // crear un buffer para armar el mensaje
        ByteBuffer buff = Conector.newBuffer(MSJ_GOLPE,
            0);
        
        // ingresar los datos especificos
        
        // empaquetar el buffer y enviarlo
        return Conector.enviaMsj(Conector.buf2arr(buff), destino);
    }
    
    public static boolean sendDisparo(String destino) {
        // crear un buffer para armar el mensaje
        ByteBuffer buff = Conector.newBuffer(MSJ_DISPARO,
            0);
        
        // ingresar los datos especificos
        
        // empaquetar el buffer y enviarlo
        return Conector.enviaMsj(Conector.buf2arr(buff), destino);
    }
    
    public static boolean sendPlayer(String destino) {
        // crear un buffer para armar el mensaje
        ByteBuffer buff = Conector.newBuffer(MSJ_PLAYER,
            0);
        
        // ingresar los datos especificos
        
        // empaquetar el buffer y enviarlo
        return Conector.enviaMsj(Conector.buf2arr(buff), destino);
    }
    
    public static boolean sendNPC(String destino) {
        // crear un buffer para armar el mensaje
        ByteBuffer buff = Conector.newBuffer(MSJ_NPC,
            0);
        
        // ingresar los datos especificos
        
        // empaquetar el buffer y enviarlo
        return Conector.enviaMsj(Conector.buf2arr(buff), destino);
    }
    
    public static boolean sendLobby(String destino) {
        // crear un buffer para armar el mensaje
        ByteBuffer buff = Conector.newBuffer(MSJ_LOBBY,
            0);
        
        // ingresar los datos especificos
        
        // empaquetar el buffer y enviarlo
        return Conector.enviaMsj(Conector.buf2arr(buff), destino);
    }
    
    public static boolean sendResult(String destino) {
        // crear un buffer para armar el mensaje
        ByteBuffer buff = Conector.newBuffer(MSJ_RESULT,
            0);
        
        // ingresar los datos especificos
        
        // empaquetar el buffer y enviarlo
        return Conector.enviaMsj(Conector.buf2arr(buff), destino);
    }
    
    public static boolean sendPlano(String destino) {
        // crear un buffer para armar el mensaje
        ByteBuffer buff = Conector.newBuffer(MSJ_PLANO, 0);
        
        // empaquetar el buffer y enviarlo
        return Conector.enviaMsj(Conector.buf2arr(buff), destino);
    }
    
    public static boolean sendMundo(String destino) {
        // crear un buffer para armar el mensaje
        ByteBuffer buff = Conector.newBuffer(MSJ_MUNDO,
            0);
        
        // ingresar los datos especificos
        
        // empaquetar el buffer y enviarlo
        return Conector.enviaMsj(Conector.buf2arr(buff), destino);
    }
}
