package com.bebeplace.bebeplaceapi.common.web

data class PagedResponse<T>(
    val content: List<T>,
    val page: PageInfo,
    val totalElements: Long,
    val totalPages: Int
) {
    companion object {
        fun <T> of(
            content: List<T>,
            pageNumber: Int,
            pageSize: Int,
            totalElements: Long
        ): PagedResponse<T> {
            val totalPages = if (totalElements == 0L) 0 else ((totalElements - 1) / pageSize + 1).toInt()
            
            return PagedResponse(
                content = content,
                page = PageInfo(
                    number = pageNumber,
                    size = pageSize,
                    totalElements = totalElements.toInt(),
                    totalPages = totalPages
                ),
                totalElements = totalElements,
                totalPages = totalPages
            )
        }
    }
}

data class PageInfo(
    val number: Int,
    val size: Int,
    val totalElements: Int,
    val totalPages: Int
)