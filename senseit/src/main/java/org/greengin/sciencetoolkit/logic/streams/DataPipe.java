package org.greengin.sciencetoolkit.logic.streams;

import java.util.Vector;

public class DataPipe {

	DataOutput origin;
	Vector<DataInputOutput> filters;
	DataInput end;
	
	public DataPipe(DataOutput origin) {
		this.origin = origin;
		this.filters = new Vector<DataInputOutput>();
		this.end = null;
	}
	
	public void addFilter(DataInputOutput filter) {
		this.filters.add(filter);
	}
	
	public void setEnd(DataInput end) {
		this.end = end;
	}
	
	public void attach() {
		DataOutput last = origin;
		for (DataInputOutput filter : filters) {
			last.addInput(filter);
			last = filter;
		}
		last.addInput(end);	
	}
	
	public void detach() {
		DataOutput last = origin;
		for (DataInputOutput filter : filters) {
			last.removeInput(filter);
			last = filter;
		}
		last.removeInput(end);
	}
	

}
