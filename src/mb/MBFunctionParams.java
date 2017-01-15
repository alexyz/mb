package mb;

public class MBFunctionParams implements Cloneable {
	public int depth;
	public double bound;
	public Complex power;
	@Override
	public MBFunctionParams clone () {
		try {
			return (MBFunctionParams) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}