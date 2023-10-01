package moe.gensoukyo.gui.pages

import moe.gensoukyo.gui.pages.collection.CollectionPageTool

object Pages {
    val pages = mapOf(
        "测试UI" to null,
        "强化UI" to EnhancePageTools,
        "熟练度UI" to ProficiencyPageTools,
        "分解UI" to DecomposePageTools,
        "镶嵌UI" to EmbeddingPageTools,
        "摘除镶嵌UI" to UnEmbeddingPageTools,
        "collection_mobs" to CollectionPageTool,
        "collection_akyuu" to CollectionPageTool,
        "collection_mooncake" to CollectionPageTool
    )
}
