package logic.sincronia;
// tiene los metodos para comunicacion saliente

import java.nio.ByteBuffer;
import logic.interfaz.Adsobalin;
import logic.interfaz.Lobby;
import logic.interfaz.Mundo;

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
    
    // incremento del servidor para su ping
    private static byte server_orden = 0;
    
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
    
    public static boolean sendNPC() {
        // crear un buffer para armar el mensaje
        ByteBuffer buff = Conector.newBuffer(MSJ_NPC,
            1 + Float.BYTES + Integer.BYTES * 3 +
            Integer.BYTES * 18 + 1 + (Adsobalin.NAME_LEN + 1) +
            18 * (Float.BYTES * 3 + 2));
        
        // ingresar los datos especificos
        buff.put(putServerOrden());
        Mundo mun;
        try {
            mun = (Mundo)Adsobalin.raiz.getScene();
        }
        catch (Exception e) {
            return false;
        }
        buff.putFloat(Mundo.tiempoRestante);
        buff.putInt(Mundo.radioMundo);
        buff.putInt(Adsobalin.gruPoints[0]);
        buff.putInt(Adsobalin.gruPoints[1]);
        for (int i = 0; i < 18; i++) {
            buff.putInt(Adsobalin.userPoints[i]);
        }
        int utr = Adsobalin.userBestPoints();
        buff.put((byte)utr);
        if (utr == -1) {
            Conector.buffPutString(buff, "");
        }
        else {
            Conector.buffPutString(buff, Adsobalin.userName[utr]);
        }
        float[] npc;
        for (int i = 0; i < 18; i++) {
            npc = mun.getNPC(i);
            buff.putFloat(npc[0]); // x
            buff.putFloat(npc[1]); // y
            buff.putFloat(npc[2]); // ang
            buff.put((byte)npc[3]); // hit
            buff.put((byte)npc[4]); // inmune
        }
        
        // empaquetar el buffer y enviarlo
        Conector.enviaAll(Conector.buf2arr(buff), "");
        return true;
    }
    
    public static boolean sendLobby() {
        // crear un buffer para armar el mensaje
        ByteBuffer buff = Conector.newBuffer(MSJ_LOBBY,
            1 + 4 + 18 * (1 + (Adsobalin.NAME_LEN + 3)));
        
        // ingresar los datos especificos
        buff.put(putServerOrden());
        Lobby lob;
        try {
            lob = (Lobby)Adsobalin.raiz.getScene();
        }
        catch (Exception e) {
            return false;
        }
        buff.put((byte)lob.getDatos("talla"));
        buff.put((byte)lob.getDatos("obstaculos"));
        buff.put((byte)lob.getDatos("tiempo"));
        buff.put((byte)lob.getDatos("encursable"));
        String[] n = lob.getNombres();
        for (int i = 0; i < 18; i++) {
            buff.put((byte)lob.getDatos("npc" + i));
            Conector.buffPutString(buff, n[i]);
        }
        
        // empaquetar el buffer y enviarlo
        Conector.enviaAll(Conector.buf2arr(buff), "");
        return true;
    }
    
    public static void sendResult() {
        // crear un buffer para armar el mensaje
        ByteBuffer buff = Conector.newBuffer(MSJ_RESULT,
            0);
        
        // ingresar los datos especificos
        
        // empaquetar el buffer y enviarlo
        Conector.enviaAll(Conector.buf2arr(buff), "");
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
    
    private static byte putServerOrden() {
        byte b = server_orden;
        server_orden++;
        return b;
    }
}
