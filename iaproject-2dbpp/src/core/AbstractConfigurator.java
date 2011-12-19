package core;

import logic.ProblemConfiguration;
import gui.OptimumPainter;

public abstract class AbstractConfigurator<T> implements CoreDescriptor {
	
	public AbstractConfigurator() {
		/* force the existence of a no-parameter constructor (in fact all
		 * descriptors should have no constructor parameters).
		 */
	}
	
	public final CoreController getConfiguredInstance(final ProblemConfiguration problemConf, OptimumPainter painter) throws DataParsingException {
		final T coreConf = createCoreConfiguration();
		
		CoreConfiguration<T> cc = new CoreConfiguration<T>() {
			@Override
			public ProblemConfiguration getProblemConfiguration() {
				return problemConf;
			}

			@Override
			public T getCoreConfiguration() {
				return coreConf;
			}
		};
		AbstractCore<T, ?> core = getConfiguredCore(cc, painter);
		
		return core.getController();
	}
	
	@Override
	public final T getCoreConfiguration() throws DataParsingException {
		return createCoreConfiguration();
	}
	
	@Override
	public final void setCoreConfiguration(Object conf) throws ClassCastException {
		@SuppressWarnings("unchecked")
		T specConf = (T) conf;
		
		setConfiguration(specConf);
	}
	
	/**
	 * Use the given one as specific configuration for the core. This method
	 * should also set configuration values to the Component returned by
	 * {@link #getConfigurationComponent()}.
	 * @param config
	 */
	protected abstract void setConfiguration(T config);
	
	/**
	 * This method should return a new core instance using <tt>conf</tt>
	 * as configuration and <tt>painter<tt> to paint optimum.
	 * 
	 * @param conf
	 * @param painter
	 * @return
	 * @throws DataParsingException
	 */
	protected abstract AbstractCore<T, ?> getConfiguredCore(CoreConfiguration<T> conf, OptimumPainter painter);
	
	/**
	 * This method should parse component returned by 
	 * {@link #getConfigurationComponent()} and, with found values, build
	 * a core-specific configuration object, returning it.
	 * 
	 * @return
	 * @throws DataParsingException
	 */
	protected abstract T createCoreConfiguration() throws DataParsingException;
	
}
