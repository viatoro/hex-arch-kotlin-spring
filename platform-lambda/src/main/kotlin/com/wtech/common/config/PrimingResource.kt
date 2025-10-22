package com.wtech.common.config

import org.crac.Context
import org.crac.Resource
import org.springframework.context.annotation.Configuration

@Configuration
class PrimingResource : Resource {
    override fun beforeCheckpoint(p0: Context<out Resource?>?) {
        TODO("Not yet implemented")
    }

    override fun afterRestore(p0: Context<out Resource?>?) {
        TODO("Not yet implemented")
    }
}
