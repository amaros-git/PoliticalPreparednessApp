package com.example.android.politicalpreparedness.data.database.representativescache

import com.example.android.politicalpreparedness.data.models.*
import com.example.android.politicalpreparedness.representative.model.Representative

fun convertRepresentativeToCacheItem(
        representative: Representative,
        address: Address
) =
        RepresentativeCacheDataItem(
                0,
                getCityState(address),
                address.city,
                address.state,
                representative.official.name,
                representative.official.party,
                representative.official.photoUrl,
                getTwitterId(representative.official.channels),
                getFacebookId(representative.official.channels),
                representative.official.urls?.first(),
                representative.office.division.id
        )

fun getCityState(address: Address) = address.city + address.state

private fun getFacebookId(channels: List<Channel>?): String? {
    return channels?.filter { channel -> channel.type == "Facebook" }
            ?.map { channel -> channel.id }
            ?.firstOrNull()
}

private fun getTwitterId(channels: List<Channel>?): String? {
    return channels?.filter { channel -> channel.type == "Twitter" }
            ?.map { channel -> channel.id }
            ?.firstOrNull()
}


fun convertCacheItemToRepresentative(
        representativeCacheItem: RepresentativeCacheDataItem
) =
        Representative(
                createOfficial(representativeCacheItem),
                createOffice(representativeCacheItem)
        )

private fun createListOfChannels(cacheItem: RepresentativeCacheDataItem): List<Channel>? {
    val channels = mutableListOf<Channel>()
    if (null != cacheItem.facebookId) {
        channels.add(Channel("Facebook", cacheItem.facebookId))
    }
    if (null != cacheItem.twitterId) {
        channels.add(Channel("Twitter", cacheItem.twitterId))
    }

    return if (channels.isEmpty()) null else channels
}

private fun createOfficial(
        representativeCacheItem: RepresentativeCacheDataItem
): Official {
    val urls = if (representativeCacheItem.wwwUrl.isNullOrBlank()) {
        null
    } else {
        listOf(representativeCacheItem.wwwUrl)
    }
    val channels = createListOfChannels(representativeCacheItem)

    return Official(
            representativeCacheItem.name,
            null,
            representativeCacheItem.partyName,
            null,
            urls,
            representativeCacheItem.photoUrl,
            channels)
}

private fun createOffice(representativeCacheItem: RepresentativeCacheDataItem) =
        Office(
                representativeCacheItem.name,
                Division(
                        representativeCacheItem.divisionId,
                        representativeCacheItem.cityState,
                        representativeCacheItem.cityState
                ),
                emptyList()
        )
