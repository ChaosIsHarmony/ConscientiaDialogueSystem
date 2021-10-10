package cds.utils;

public class JsonValue<E> {

	private E value;

	public JsonValue(E value) {
		this.value = value;
	}

	public E getValue() { return value; }
	public void setValue(E value) { this.value = value; }
}
