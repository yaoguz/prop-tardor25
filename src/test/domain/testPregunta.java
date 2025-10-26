package domain;

import static org.junit.Assert.*;
import org.junit.Test;
import domain.Pregunta;

/**
 * Clase testeadora de Pregunta
 */
 
public class TestPregunta {
	@Test
	public void constructoraOk() {
	  	Pregunta p = new Pregunta(5, 6, "test");
	  	assertEquals(5, p.getIdEncuesta);
	  	assertEquals(6, p.getIdPregunta);
	  	assertEquals("test", p.getEnunciado);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructoraExceptionNull() {
		Pregunta p = new Pregunta(0, 0, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructoraExceptionVacia() {
		Pregunta p = new Pregunta(0, 0, "");
	}

	@Test
	public void getterIdEncuesta() {
		Pregunta p = new Pregunta(0, 1, "test");
		assertEquals(0, p.getIdEncuesta());
	}

	@Test
	public void getterIdPregunta() {
		Pregunta p = new Pregunta(0, 1, "test");
		assertEquals(1, p.getIdPregunta());
	}

	@Test
	public void getterEnunciado() {
		Pregunta p = new Pregunta(0, 1, "test");
		assertEquals("test", p.getEnunciado());
	}

	@Test
	public void agregarRespuesta() {
		Pregunta p = new Pregunta(0, 1, "test");
		p.addRespuesta(22);
		ArrayList<Integer> l = p.getIdRespuestas
		assertTrue(l.contains(22);
	}
  /*
  @Test
  public void divideGettingCorrectResult() {
    Main divisionMachine = new Main();

    float actualResult = divisionMachine.division(10,2);
    assertEquals(5.0, actualResult, 0.0001);
  }

  @Test(expected = ArithmeticException.class)
  public void divideByZeroThrowsException() {
    Main divisionMachine = new Main();

    float actualResult = divisionMachine.division(10,0);

  }*/
}
