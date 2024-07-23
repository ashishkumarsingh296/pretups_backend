package com.btsl.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SearchCriteria {
	
	public enum Operator 
	{
	   EQUALS, NOT_EQUALS, GREATER_THAN, LESS_THAN, IN;
	}
	
	public enum BooleanOperator 
	{
	   AND, OR;
	}
	
	public enum ValueType 
	{
	   STRING, NUMBER;
	}
	
	private String column;
	private String value;
	private Operator operator;
	private List<SearchCriteria> searchCriteriaList = new ArrayList<>();
	





	private Set<String> values;
	private BooleanOperator booleanOperator;
	private boolean nextCriteria;
	private ValueType valueType;

	
	public BooleanOperator getBooleanOperator() {
		return booleanOperator;
	}

	public void setBooleanOperator(BooleanOperator booleanOperator) {
		this.booleanOperator = booleanOperator;
	}


	public ValueType getValueType() {
		return valueType;
	}

	public void setValueType(ValueType valueType) {
		this.valueType = valueType;
	}

	public boolean isNextCriteria() {
		return nextCriteria;
	}

	public void setNextCriteria(boolean nextCriteria) {
		this.nextCriteria = nextCriteria;
	}

	public Set<String> getValues() {
		return values;
	}

	public void setValues(Set<String> values) {
		this.values = values;
	}
	
	
	
	
	public SearchCriteria(String column, Operator operator, String value){
		this.column = column;
		this.value = value;
		this.operator = operator;
		this.nextCriteria = false;
	}
	
	public SearchCriteria(String column, Operator operator, String value, ValueType valueType, String dummy){
		this.column = column;
		this.value = value;
		this.operator = operator;
		this.nextCriteria = false;
		this.valueType = valueType ;
	}
	
	
	public SearchCriteria(String column, Operator operator, Set<String> values, ValueType valueType){
		this.column = column;
		this.values = values;
		this.valueType = valueType ;
		this.operator = operator;
		this.nextCriteria = false;
	}
	
	
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Operator getOperator() {
		return operator;
	}
	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	
	public SearchCriteria addCriteria(SearchCriteria searchCriteria, BooleanOperator booleanOperator) {
		//this.searchCriteria = searchCriteria;
		searchCriteria.booleanOperator = booleanOperator;
		this.nextCriteria = true;
		searchCriteriaList.add(searchCriteria);
		return this;
	}

	public SearchCriteria getSearchCriteria() {
		
		SearchCriteria searchCriteria = searchCriteriaList.get(0) ;
		
		searchCriteriaList.remove(0);
		return searchCriteria;
	}
	
	public List<SearchCriteria> getSearchCriteriaList() {
		return searchCriteriaList;
	}

	public void setSearchCriteriaList(List<SearchCriteria> searchCriteriaList) {
		this.searchCriteriaList = searchCriteriaList;
	}	
}
