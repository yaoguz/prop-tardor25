package test;

import src.domini.*;
import static org.junit.Assert.*;
import org.junit.Test;


/**
 * Clase testeadora de Usuario
 */
public Class TestUsuario() {

    private Usuario usuario;
    private final Stirng userName = "tester";
    private final String contrasena = "123456";

    /**
     * Configuraciones iniciales
     */
    @Before
    public void inicialConf() {
        usuario = new Usuario (userName, contrasena);
    }
    
    @Test
    public void testConstructor() {
        assertEquals("Comprobar usuario creado con nombre tester",userName, usuario.getNomUsuario());
        
    }

}