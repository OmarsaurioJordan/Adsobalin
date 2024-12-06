package logic.sincronia;
// tiene los metodos para comunicacion saliente

import java.nio.ByteBuffer;
import java.util.ArrayList;
import javafx.stage.Stage;
import logic.interfaz.Adsobalin;
import logic.interfaz.Lobby;
import logic.interfaz.Mundo;
import logic.interfaz.Resultado;

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
    public static final byte MSJ_PING = 11; // C
    
    // listado de tipos de submensajes
    public static final byte SUB_VERSION = 0;
    public static final byte SUB_CUPO = 1;
    public static final byte SUB_ENCURSO = 2;
    public static final byte SUB_ESTILO = 3;
    public static final byte SUB_NOMBRE = 4;
    
    // incremento del servidor para su ping
    public static byte server_orden = 0;
    // incremento de los players para su ping
    public static byte[] players_orden = new byte[18];
    // historial reciente de proyectiles
    public static ArrayList<Integer> histoProy = new ArrayList<>();
    // historial reciente de golpes
    public static ArrayList<Integer> histoGolpe = new ArrayList<>();
    
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
    
    public static void sendGolpe(int golpeador, int golpeado,
            boolean isKill, int llave, float angulo) {
        // crear un buffer para armar el mensaje
        ByteBuffer buff = Conector.newBuffer(MSJ_GOLPE,
            3 + 2 * Integer.BYTES + Float.BYTES);
        
        // ingresar los datos especificos
        buff.put((byte)golpeador);
        buff.put((byte)golpeado);
        buff.putInt(llave);
        buff.putFloat(angulo);
        int clave = (int)(Adsobalin.DADO.nextFloat() * 9999999f);
        clave = Integer.parseInt((Adsobalin.indice + 1) + "" + clave);
        buff.putInt(clave);
        if (isKill) {
            buff.put((byte)1);
        }
        else {
            buff.put((byte)0);
        }
        
        // empaquetar el buffer y enviarlo
        if (Adsobalin.isServer) {
            Conector.enviaAll(Conector.buf2arr(buff), "");
        }
        else {
            Conector.enviaMsj(Conector.buf2arr(buff), Conector.myServer);
        }
    }
    
    public static void sendDisparo(int ind, int llave,
            float[] posicion, float direccion, boolean isFromNPC) {
        // crear un buffer para armar el mensaje
        ByteBuffer buff = Conector.newBuffer(MSJ_DISPARO,
            2 + Integer.BYTES + Float.BYTES * 3);
        
        // ingresar los datos especificos
        buff.putInt(llave);
        buff.put((byte)ind);
        buff.putFloat(posicion[0]);
        buff.putFloat(posicion[1]);
        buff.putFloat(direccion);
        if (isFromNPC) {
            buff.put((byte)1);
        }
        else {
            buff.put((byte)0);
        }
        
        // empaquetar el buffer y enviarlo
        if (Adsobalin.isServer) {
            Conector.enviaAll(Conector.buf2arr(buff), "");
        }
        else {
            Conector.enviaMsj(Conector.buf2arr(buff), Conector.myServer);
        }
    }
    
    public static boolean sendPlayer(Stage raiz, String destino) {
        // verificar la informacionn a enviar
        Mundo mun;
        try {
            mun = (Mundo)raiz.getScene();
        }
        catch (Exception e) {
            return false;
        }
        
        // crear un buffer para armar el mensaje
        ByteBuffer buff = Conector.newBuffer(MSJ_PLAYER,
            5 + Float.BYTES * 3 + (Adsobalin.NAME_LEN + 1));
        
        // ingresar los datos especificos
        buff.put(putPlayerOrden());
        buff.put((byte)Adsobalin.indice);
        float[] ply = mun.getPlayer();
        buff.putFloat(ply[0]); // x
        buff.putFloat(ply[1]); // y
        buff.putFloat(ply[2]); // ang
        buff.put((byte)ply[3]); // hit
        buff.put((byte)ply[4]); // inmune
        buff.put((byte)ply[5]); // estilo
        Conector.buffPutString(buff, Adsobalin.nombre);
        
        // empaquetar el buffer y enviarlo
        Conector.enviaMsj(Conector.buf2arr(buff), destino);
        return true;
    }
    
    public static boolean sendNPC(Stage raiz) {
        // verificar la informacionn a enviar
        Mundo mun;
        try {
            mun = (Mundo)raiz.getScene();
        }
        catch (Exception e) {
            return false;
        }
        
        // crear un buffer para armar el mensaje
        ByteBuffer buff = Conector.newBuffer(MSJ_NPC,
            1 + Float.BYTES + Integer.BYTES * 3 +
            Integer.BYTES * 18 + 2 + 2 * (Adsobalin.NAME_LEN + 1) +
            18 * (Float.BYTES * 3 + 3));
        
        // ingresar los datos especificos
        buff.put(putServerOrden());
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
        buff.put((byte)Adsobalin.indice);
        Conector.buffPutString(buff, Adsobalin.nombre);
        float[] npc;
        for (int i = 0; i < 18; i++) {
            if (Adsobalin.indice == i) {
                npc = mun.getPlayer();
            }
            else {
                npc = mun.getNPC(i);
            }
            buff.putFloat(npc[0]); // x
            buff.putFloat(npc[1]); // y
            buff.putFloat(npc[2]); // ang
            buff.put((byte)npc[3]); // hit
            buff.put((byte)npc[4]); // inmune
            buff.put((byte)npc[5]); // estilo
        }
        
        // empaquetar el buffer y enviarlo
        Conector.enviaAll(Conector.buf2arr(buff), "");
        return true;
    }
    
    public static boolean sendLobby(Stage raiz) {
        // verificar la informacionn a enviar
        Lobby lob;
        try {
            lob = (Lobby)raiz.getScene();
        }
        catch (Exception e) {
            return false;
        }
        
        // crear un buffer para armar el mensaje
        ByteBuffer buff = Conector.newBuffer(MSJ_LOBBY,
            1 + 4 + 18 * (1 + (Adsobalin.NAME_LEN + 1)));
        
        // ingresar los datos especificos
        buff.put(putServerOrden());
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
    
    public static boolean sendResult(Stage raiz) {
        // verificar la informacionn a enviar
        Resultado res;
        try {
            res = (Resultado)raiz.getScene();
        }
        catch (Exception e) {
            return false;
        }
        String txt = res.getAllData();
        
        // crear un buffer para armar el mensaje
        ByteBuffer buff = Conector.newBuffer(MSJ_RESULT,
            1 + (txt.length() + 1));
        
        // ingresar los datos especificos
        buff.put(putServerOrden());
        Conector.buffPutString(buff, txt);
        
        // empaquetar el buffer y enviarlo
        Conector.enviaAll(Conector.buf2arr(buff), "");
        return true;
    }
    
    public static boolean sendPlano(String destino) {
        // crear un buffer para armar el mensaje
        ByteBuffer buff = Conector.newBuffer(MSJ_PLANO, 0);
        
        // empaquetar el buffer y enviarlo
        return Conector.enviaMsj(Conector.buf2arr(buff), destino);
    }
    
    public static boolean sendPing(String destino) {
        // crear un buffer para armar el mensaje
        ByteBuffer buff = Conector.newBuffer(MSJ_PING, 0);
        
        // empaquetar el buffer y enviarlo
        return Conector.enviaMsj(Conector.buf2arr(buff), destino);
    }
    
    public static boolean sendMundo(Stage raiz, String destino) {
        // verificar y obtener la informacion a enviar
        Mundo mun;
        try {
            mun = (Mundo)raiz.getScene();
        }
        catch (Exception e) {
            return false;
        }
        ArrayList<Float> arboles = mun.getArboles();
        ArrayList<Float> cdvrs = mun.getCadaveres();
        
        // crear un buffer para armar el mensaje
        ByteBuffer buff = Conector.newBuffer(MSJ_MUNDO,
                Integer.BYTES * 2 +
                (arboles.size() + cdvrs.size()) * Float.BYTES);
        
        // ingresar los datos especificos
        buff.putInt(arboles.size() / 2);
        for (int i = 0; i < arboles.size(); i++) {
            buff.putFloat(arboles.get(i));
        }
        buff.putInt(cdvrs.size() / 4);
        for (int i = 0; i < cdvrs.size(); i++) {
            buff.putFloat(cdvrs.get(i));
        }
        
        // empaquetar el buffer y enviarlo
        return Conector.enviaMsj(Conector.buf2arr(buff), destino);
    }
    
    private static byte putServerOrden() {
        byte b = server_orden;
        server_orden++;
        return b;
    }
    
    public static boolean apruebaServerOrden(byte newOrden) {
        if (newOrden > server_orden || (server_orden - newOrden) > 127) {
            server_orden = newOrden;
            return true;
        }
        return false;
    }
    
    private static byte putPlayerOrden() {
        byte b = players_orden[Adsobalin.indice];
        players_orden[Adsobalin.indice]++;
        return b;
    }
    
    public static boolean apruebaPlayerOrden(int indice, byte newOrden) {
        byte actual = players_orden[indice];
        if (newOrden > actual || (actual - newOrden) > 127) {
            players_orden[indice] = newOrden;
            return true;
        }
        return false;
    }
    
    public static void incrementaPing(String ip) {
        int ind = Adsobalin.userGetInd(ip);
        if (ind != -1) {
            Adsobalin.userPing[ind] = Conector.PING;
        }
    }
    
    public static void setServerPing() {
        Conector.serverPing = Conector.PING;
    }
    
    public static boolean verifyProy(int llave) {
        if (!histoProy.contains(llave)) {
            histoProy.add(llave);
            // cuando se superen las 100 entradas, borrara las 50 mas antiguas
            if (histoProy.size() > 100) {
                histoProy.subList(0, 50).clear();
            }
            return true;
        }
        return false;
    }
    
    public static boolean verifyGolpe(int clave) {
        if (!histoGolpe.contains(clave)) {
            histoGolpe.add(clave);
            // cuando se superen las 100 entradas, borrara las 50 mas antiguas
            if (histoGolpe.size() > 100) {
                histoGolpe.subList(0, 50).clear();
            }
            return true;
        }
        return false;
    }
}
