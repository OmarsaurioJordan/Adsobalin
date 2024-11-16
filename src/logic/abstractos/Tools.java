package logic.abstractos;
// herramientas de uso general

public abstract class Tools {
    
    public static float NO_COLI = -100f;
    
    public static float[] vecSuma(float[] vec1, float[] vec2) {
        float[] res = {vec1[0] + vec2[0], vec1[1] + vec2[1]};
        return res;
    }
    
    public static float[] vecResta(float[] vec1, float[] vec2) {
        float[] res = {vec1[0] - vec2[0], vec1[1] - vec2[1]};
        return res;
    }
    
    public static float vecDistancia(float[] vec1, float[] vec2) {
        float[] dif = vecResta(vec1, vec2);
        float res = (float)Math.sqrt(Math.pow(dif[0], 2f) +
                Math.pow(dif[1], 2f));
        return res;
    }
    
    public static float vecDireccion(float[] vec1, float[] vec2) {
        float[] dif = vecResta(vec2, vec1);
        float res = (float)Math.atan2(dif[1], dif[0]);
        return res;
    }
    
    public static float[] vecEscala(float[] vec, float escala) {
        float[] res = {vec[0] * escala, vec[1] * escala};
        return res;
    }
    
    public static float[] vecDirector(float dir) {
        float[] res = {(float)Math.cos(dir), (float)Math.sin(dir)};
        return res;
    }
    
    public static float[] vecMover(float[] ini, float dist, float dir) {
        float[] res = vecSuma(ini, vecEscala(vecDirector(dir), dist));
        return res;
    }
    
    public static float circleColision(float[] pos1, float radio1,
            float[] pos2, float radio2) {
        // retorna direccion de rebote normal o NO_COLI si no hubo colision
        float dist = vecDistancia(pos1, pos2);
        if (dist < radio1 + radio2) {
            return vecDireccion(pos1, pos2);
        }
        return NO_COLI;
    }
    
    public static float[] circleLimitar(float[] centro, float radio,
            float[] pos) {
        // mantiene la posicion dentro del circulo
        if (vecDistancia(centro, pos) > radio) {
            float dir = vecDireccion(centro, pos);
            float[] res = vecMover(centro, radio, dir);
            return res;
        }
        return pos;
    }
    
    public static float[] vecInterpolar(float[] vec1, float[] vec2,
            float proporcion, float limite) {
        // vec1 se acerca a vec2, proporcion modifica la velocidad de
        // acercamiento y limite impone la velocidad maxima
        float dist = vecDistancia(vec1, vec2);
        float dir = vecDireccion(vec1, vec2);
        dist = Math.min(Math.min(dist, dist * proporcion), limite);
        float[] res = vecMover(vec1, dist, dir);
        return res;
    }
    
    public static float angDifference(float ang1, float ang2) {
        // normalizar los angulos entre 0 y 2*pi
        float maxAng = 2f * (float)Math.PI;
        ang1 = (ang1 % maxAng + maxAng) % maxAng;
        ang2 = (ang2 % maxAng + maxAng) % maxAng;
        // Calcular la diferencia mínima
        float delta = ang2 - ang1;
        // ajustar delta para tomar camino mas corto
        if (delta > Math.PI) {
            delta -= maxAng;
        }
        else if (delta < -Math.PI) {
            delta += maxAng;
        }
        return delta;
    }
    
    public static float interpAngle(float actual, float deseado, float paso) {
        float delta = angDifference(actual, deseado);
        // limitar para que no oscile
        if (Math.abs(delta) <= paso) {
            // alcanzo el valor final
            return deseado;
        }
        // hacer el avance como tal, en la direccion mas corta
        return actual + (float)Math.signum(delta) * paso;
    }
}
