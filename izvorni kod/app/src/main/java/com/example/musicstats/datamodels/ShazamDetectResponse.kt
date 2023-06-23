package com.example.musicstats.datamodels

data class ShazamDetectResponse(
    val matches: List<Match>,
    val timestamp: Long,
    val timezone: String,
    val tagid: String,
    val track: TrackShazam,
    val url: String,
    val artists: List<ArtistShazam>,
    val isrc: String,
    val genres: Genres,
    val urlparams: UrlParams,
    val myshazam: MyShazam,
    val albumadamid: String,
    val sections: List<Section>
)

data class Match(
    val id: String,
    val offset: Double,
    val channel: String,
    val timeskew: Double,
    val frequencyskew: Double
)

data class TrackShazam(
    val layout: String,
    val type: String,
    val key: String,
    val title: String,
    val subtitle: String,
    val images: Images,
    val share: Share,
    val hub: Hub,
    val explicit: Boolean,
    val displayname: String
)

data class Images(
    val background: String,
    val coverart: String,
    val coverarthq: String,
    val joecolor: String
)

data class Share(
    val subject: String,
    val text: String,
    val href: String,
    val image: String,
    val twitter: String,
    val html: String,
    val avatar: String,
    val snapchat: String
)

data class Hub(
    val type: String,
    val image: String,
    val actions: List<Action>,
    val options: List<Option>,
    val providers: List<Provider>,
    val explicit: Boolean,
    val displayname: String
)

data class Action(
    val name: String,
    val type: String,
    val id: String,
    val uri: String
)

data class Option(
    val caption: String,
    val actions: List<Action>,
    val beacondata: BeaconData,
    val image: String,
    val type: String,
    val listcaption: String,
    val overflowimage: String,
    val colouroverflowimage: Boolean,
    val providername: String
)

data class BeaconData(
    val type: String,
    val providername: String
)

data class Provider(
    val caption: String,
    val images: ProviderImages,
    val actions: List<Action>,
    val type: String
)

data class ProviderImages(
    val overflow: String,
    val default: String
)

data class UrlParams(
    val tracktitle: String,
    val trackartist: String
)

data class MyShazam(
    val apple: Apple
)

data class Apple(
    val actions: List<Action>
)

data class ArtistShazam(
    val id: String,
    val adamid: String
)

data class Genres(
    val primary: String
)

data class Section(
    val type: String,
    val metapages: List<MetaPage>,
    val tabname: String,
    val metadata: List<Metadata>,
    val text: List<String>,
    val footer: String,
    val beacondata: BeaconData
)

data class MetaPage(
    val image: String,
    val caption: String
)

data class Metadata(
    val title: String,
    val text: String
)

