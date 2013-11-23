package pc;

import java.awt.Color;
import java.util.*;

import javax.xml.bind.annotation.*;

public class O {
	
}

/** point */
@XmlRootElement
class P {
	@XmlAttribute
	public double x;
	@XmlAttribute
	public double y;
	@XmlAttribute
	public double z;
	public P () {
		// 
	}
	public P (double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}

/** triangle */
@XmlRootElement
class Q {
	@XmlAttribute
	public String s;
	public P a;
	public P b;
	public P c;
	public P d;
	// FIXME needs serialiser
	public Color col;
	public Q () {
		// 
	}
	public Q (P a, P b, P c, P d, Color col) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.col = col;
	}
}

/** quad object */
@XmlRootElement
class QO {
	@XmlElement(name="q")
	public final List<Q> l = new ArrayList<>();
	public QO () {
		//
	}
}


/** triangle */
class T {
	public final P a, b, c;
	public final P colour;
	public T (P a, P b, P c, P colour) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.colour = colour;
	}
}

/** triangle object */
class TO {
	public final T[] t;
	public TO (T[] t) {
		this.t = t;
	}
}

