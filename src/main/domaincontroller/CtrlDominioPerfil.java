package domaincontroller;

import java.util.List;

import classcontroller.CtrlUsuario;

import domain.Usuario;

/**
 * Controlador de Dominio para la gestión de perfiles.
 * 
 * Responsabilidades:
 * - Obtener perfiles de usuarios
 * - Generar perfiles basados en respuestas
 * - Comparar perfiles entre usuarios
 * - Clustering y análisis de usuarios
 * 
 * NOTA: La funcionalidad completa de perfiles está pendiente de implementación.
 * 
 * @author Yaoyao Guo
 * @version 2.0
 */
public class CtrlDominioPerfil {

	/** Atributos - Controladores de clase **/
	
	private CtrlUsuario ctrlUsuario;

	/** Constructor **/

	/**
	 * Constructora. Inicializa los controladores necesarios.
	 * 
	 * @param ctrlUsuario Controlador de usuarios
	 */
	public CtrlDominioPerfil(CtrlUsuario ctrlUsuario) {
		this.ctrlUsuario = ctrlUsuario;
	}

	// ==================== GESTIÓN DE PERFILES ====================
	
	/**
	 * Obtiene los perfiles de un usuario (funcionalidad de clustering/análisis).
	 * 
	 * @param nomUsuario Nombre del usuario
	 * @return Lista de perfiles del usuario
	 */
	public List<Integer> getPerfilesUsuario(String nomUsuario) {
		Usuario u = ctrlUsuario.getUsuario(nomUsuario);
		return u.getIdPerfiles();
	}
}
