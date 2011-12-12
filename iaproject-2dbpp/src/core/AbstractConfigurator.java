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
	
	protected abstract AbstractCore<T, ?> getConfiguredCore(CoreConfiguration<T> conf, OptimumPainter painter) throws DataParsingException;
	protected abstract T createCoreConfiguration() throws DataParsingException;
	
}
