package com.tinkerpop.frames.modules;

/**
 * Implements a basic FrameClassLoaderResolver that simply returns the ClassLoader of the provided Frame Type.
 *
 * @author <a href="mailto:jesse.sightler@gmail.com">Jess Sightler</a>
 */
public class DefaultClassLoaderResolver implements FrameClassLoaderResolver {
    @Override
    public ClassLoader resolveClassLoader(Class<?> frameType) {
        return frameType.getClassLoader();
    }
}
