package persistence;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import domain.Usuario;
import domain.Encuesta;
import domain.Pregunta;
import domain.Respuesta;

/**
 * Controlador de datos del sistema
 */

public class CtrlPersistence {
    /** Atributos **/
    /**
     * Mapa que almacena los usuarios existentes del sistema.
     * <p>
     * La clave es el nombre de usuario y el valor es el objeto {@link domain.Usuario}.
     */
    private Map<String,Usuario> usuarios; 

    /**
     * Mapa que almacena las encuestas existentes del sistema.
     * <p>
     * La clave es un Integer id y el valor es el objeto {@link domain.Encuesta}.
     */
    private Map<Integer,Encuesta> encuestas;

    /**
     * Mapa que almacena las preguntas de todas las encuestas del sistema
     * <p>
     * La clave es un Integer id y el valor es el objeto {@link domain.Pregunta}.
     */
    private Map<Integer,Pregunta> preguntas;

    /**
     * Mapa que almacena las respuestas de todas las preguntas del sistema
     * <p>
     * La clave es un Integer id y el valor es el objeto {@link domain.Respuesta}.
     */
    private Map<Integer,Respuesta> respuestas;


    /** Constructor y metodos de inicializacion **/

	public CtrlPersistence() {
		inicializarCtrlPersistence();
	}


    /**
     * Inicialización de los atributos de la clase
     */
	public void inicializarCtrlPersistence() {
        usuarios = new HashMap();
        encuestas = new HashMap();
        preguntas = new HashMap();
        respuestas = new HashMap();
    }

    /**
	 * Funciones que se llaman desde el controlador de Dominio. 
	 * <p>
     * Gestión de datos del sistema.
	 **/

    
    /**
    * Devuelve una lista de usuarios cuyos nombres coinciden con los especificados.
    *
    * <p>
    * Para cada nombre en la lista {@code nameUsuarios}, el método busca en el
    * mapa {@code usuarios} y añade al resultado los usuarios encontrados.
    *
    * @param nameUsuarios lista con los nombres de los usuarios que se desean obtener
    * @return una lista con los objetos {@link Usuario} cuyos nombres coinciden
    */

    public ArrayList<Usuario> getUsuarios(ArrayList<String> nameUsuarios) {
        ArrayList<Usuario> res = new ArrayList();
        
        for (String name: nameUsuarios) {
            Usuario u = usuarios.get(name);

            res.add(u);
        }

        return res;
    }

    /**
    * Devuelve una lista de encuestas cuyos ids coinciden con los especificados.
    *
    * <p>
    * Para cada id en la lista {@code idEncuestas}, el método busca en el
    * mapa {@code encuestas} y añade al resultado las encuestas encontrados.
    *
    * @param idEncuestas lista con los ids de las encuestas que se desean obtener
    * @return una lista con los objetos {@link Encuesta} cuyos ids coinciden
    */
    public ArrayList<Encuesta> getEncuestas(ArrayList<Integer> idEncuestas) {
        ArrayList<Encuesta> res = new ArrayList();
        
        for (Integer id: idEncuestas) {
            Encuesta e = encuestas.get(id);

            res.add(e);
        }

        return res;
    }

    /**
    * Devuelve una lista de preguntas cuyos ids coinciden con los especificados.
    *
    * <p>
    * Para cada id en la lista {@code idPreguntas}, el método busca en el
    * mapa {@code preguntas} y añade al resultado las preguntas encontrados.
    *
    * @param idPreguntas lista con los ids de las preguntas que se desean obtener
    * @return una lista con los objetos {@link Pregunta} cuyos ids coinciden
    */

    public ArrayList<Pregunta> getPreguntas(ArrayList<Integer> idPreguntas) {
        ArrayList<Pregunta> res = new ArrayList();

        for (Integer id: idPreguntas) {
            Pregunta p = preguntas.get(id);

            res.add(p);
        }
        
        return res;
    }

    /**
    * Devuelve una lista de respustas cuyos ids coinciden con los especificados.
    *
    * <p>
    * Para cada id en la lista {@code idRespuestas}, el método busca en el
    * mapa {@code respuestas} y añade al resultado las respuestas encontrados.
    *
    * @param idRespuestas lista con los ids de las respuestas que se desean obtener
    * @return una lista con los objetos {@link Respuesta} cuyos ids coinciden
    */

    public ArrayList<Respuesta> getRespuestas(ArrayList<Integer> idRespuestas) {
        ArrayList<Respuesta> res = new ArrayList();

        for (Integer id: idRespuestas) {
            Respuesta r = respuestas.get(id);

            res.add(r);
        }

        return res;
    }

    /**
    * Devuelve un usuario cuyo name coincide con el especificado.
    *
    * <p>
    * Para el name de {@code userName}, el método busca en el
    * mapa {@code usuarios} y devuelve el usuario encontrado.
    *
    * @param userName name del usuario que se desea obtener
    * @return un objeto {@link Usuario} cuyo name coincide
    */
    public Usuario getUsuario (String userName) {
        Usuario u = usuarios.get(userName);

        return u;
    }

    /**
    * Devuelve una encuesta cuyo id coincide con el especificado.
    *
    * <p>
    * Para el id de {@code idEncuesta}, el método busca en el
    * mapa {@code encuestas} y devuelve la encuesta encontrado.
    *
    * @param idEncuesta id de la encuesta que se desea obtener
    * @return un objeto {@link Encuesta} cuyo id coincide
    */
    public Encuesta getEncuesta (Integer idEncuesta) {
        Encuesta e = encuestas.get(idEncuesta);

        return e;
    }

    /**
    * Devuelve una pregunta cuyo id coincide con el especificado.
    *
    * <p>
    * Para el id de {@code idPregunta}, el método busca en el
    * mapa {@code preguntas} y devuelve la pregunta encontrado.
    *
    * @param idPregunta id de la pregunta que se desea obtener
    * @return un objeto {@link Pregunta} cuyo id coincide
    */
    public Pregunta getPregunta (Integer idPregunta) {
        Pregunta p = preguntas.get(idPregunta);

        return p;
    }

    /**
    * Devuelve una respuesta cuyo id coincide con el especificado.
    *
    * <p>
    * Para el id de {@code idRespuesta}, el método busca en el
    * mapa {@code respuestas} y devuelve la respuesta encontrado.
    *
    * @param idRespuesta id de la respuesta que se desea obtener
    * @return un objeto {@link Respuesta} cuyo id coincide
    */
    public Respuesta getRespuesta (Integer idRespuesta) {
        Respuesta r = respuestas.get(idRespuesta);

        return r;
    }
}