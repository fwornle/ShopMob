package com.tanfra.shopmob.features.common.arch

import kotlinx.coroutines.flow.Flow

interface ActionProcessor<Action, Mutation, Event> {
    operator fun invoke(action: Action): Flow<Pair<Mutation?, Event?>>
}
