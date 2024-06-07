package sae.card;

import java.util.Objects;

public record Point(int i, int j) {

	public Point {
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Point point
		&& i == point.i
		&& j == point.j;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(i, j);
	}
	
	public Point sum(Point otherPoint) {
		Objects.requireNonNull(otherPoint);
		return new Point(this.i + otherPoint.i, this.j + otherPoint.j);
	}

}
