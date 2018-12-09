package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Customer;
import javafx.util.Pair;

import java.util.List;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{
	Customer customer;
	List<Pair<String,Integer>> bookAndTick;


	public APIService(String name, Customer customer, List<Pair<String,Integer>> bookAndTick) {
		super(name);
		this.customer=customer;
		this.bookAndTick=bookAndTick;
	}
	public APIService(){
		super("Change_This_Name");
	}
	@Override
	protected void initialize() {
		// TODO Implement thisl,
		
	}

}
