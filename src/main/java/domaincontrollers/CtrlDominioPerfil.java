package domaincontrollers;

import java.util.ArrayList;
import java.util.List;

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
 * @author Alessandro Jin
 */
public class CtrlDominioPerfil {

	/**
	 * Constructora vacía (sin dependencias almacenadas).
	 * CtrlDominioPerfil no gestiona datos propios, solo hace análisis.
	 */
	public CtrlDominioPerfil() {}
	
	/**
	 * Obtiene los perfiles de un usuario (funcionalidad de clustering/análisis).
	 * 
	 * @param nomUsuario Nombre del usuario
	 * @param ctrlDominioUsuario Controlador de usuarios (inyección por parámetros)
	 * @return Lista de perfiles del usuario
	 */
	public List<Integer> getPerfilesUsuario(String nomUsuario, CtrlDominioUsuario ctrlDominioUsuario) {
		Usuario u = ctrlDominioUsuario.getUsuario(nomUsuario);
		return new ArrayList<>(u.getIdPerfiles());
	}
}
