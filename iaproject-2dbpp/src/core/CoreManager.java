package core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import core.BLFTest.BLFTestConfigurator;
import core.dummy.DummyConfigurator;
import core.genetic.GeneticConfigurator;
import core.tournament.TournamentConfigurator;
import core.taboo.TabooConfigurator;

public class CoreManager {
	
	/* using Class object just to avoid instantiation of all CoreDescriptors:
	 * they will be created only if needed.
	 */
	private static final Map<String, Class<? extends CoreDescriptor>> CORES;
	
	static {
		Map<String, Class<? extends CoreDescriptor>> map = new HashMap<String, Class<? extends CoreDescriptor>>();
		
		// TODO add here all CoreDescriptors with their names
		map.put("Dummy", DummyConfigurator.class);
		map.put("Genetic", GeneticConfigurator.class);
		map.put("Genetic (Tournament)", TournamentConfigurator.class);
		map.put("Tabu", TabooConfigurator.class);
		map.put("BLFTest", BLFTestConfigurator.class);
		// ...
		
		CORES = Collections.unmodifiableMap(map);
	}
	
	/**
	 * 
	 * @return a map where the String states descriptor
	 * name and the class can be used to instantiate relative descriptor
	 * (if needed).
	 */
	public static Map<String, Class<? extends CoreDescriptor>> getCores() {
		return CORES;
	}
	
}
