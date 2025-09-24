package com.bebeplace.bebeplaceapi.product.domain.model

enum class DetailedProductCategory(
    val displayName: String,
    val parentCategory: ProductCategory
) {
    // 아기 의류
    BABY_CLOTHING_ALL("전체", ProductCategory.BABY_CLOTHING),
    BABY_CLOTHING_ROMPER("우주복/슈트", ProductCategory.BABY_CLOTHING),
    BABY_CLOTHING_SWADDLE("싸개/배냇저고리", ProductCategory.BABY_CLOTHING),
    BABY_CLOTHING_DRESS("원피스", ProductCategory.BABY_CLOTHING),
    BABY_CLOTHING_TOP("상의", ProductCategory.BABY_CLOTHING),
    BABY_CLOTHING_BOTTOM("하의", ProductCategory.BABY_CLOTHING),
    BABY_CLOTHING_UNDERWEAR("내의/속옷", ProductCategory.BABY_CLOTHING),
    BABY_CLOTHING_JACKET("자켓/점퍼", ProductCategory.BABY_CLOTHING),
    BABY_CLOTHING_SWIMWEAR("수영복", ProductCategory.BABY_CLOTHING),
    BABY_CLOTHING_FORMAL_DRESS("드레스", ProductCategory.BABY_CLOTHING),
    BABY_CLOTHING_ETC("기타", ProductCategory.BABY_CLOTHING),

    // 여아 의류
    GIRLS_CLOTHING_ALL("전체", ProductCategory.GIRLS_CLOTHING),
    GIRLS_CLOTHING_DRESS("원피스", ProductCategory.GIRLS_CLOTHING),
    GIRLS_CLOTHING_SET("상하복 세트", ProductCategory.GIRLS_CLOTHING),
    GIRLS_CLOTHING_JACKET("자켓/점퍼", ProductCategory.GIRLS_CLOTHING),
    GIRLS_CLOTHING_COAT("코트/정장", ProductCategory.GIRLS_CLOTHING),
    GIRLS_CLOTHING_CARDIGAN("가디건/조끼", ProductCategory.GIRLS_CLOTHING),
    GIRLS_CLOTHING_KNIT("니트/스웨터", ProductCategory.GIRLS_CLOTHING),
    GIRLS_CLOTHING_BLOUSE("블라우스/셔츠", ProductCategory.GIRLS_CLOTHING),
    GIRLS_CLOTHING_TSHIRT("티셔츠", ProductCategory.GIRLS_CLOTHING),
    GIRLS_CLOTHING_PANTS("바지", ProductCategory.GIRLS_CLOTHING),
    GIRLS_CLOTHING_SKIRT("치마", ProductCategory.GIRLS_CLOTHING),
    GIRLS_CLOTHING_SPORTS("스포츠/테마의류", ProductCategory.GIRLS_CLOTHING),
    GIRLS_CLOTHING_UNDERWEAR("속옷/잠옷", ProductCategory.GIRLS_CLOTHING),
    GIRLS_CLOTHING_ACCESSORIES("액세서리/모자/양말", ProductCategory.GIRLS_CLOTHING),
    GIRLS_CLOTHING_SHOES("신발", ProductCategory.GIRLS_CLOTHING),
    GIRLS_CLOTHING_ETC("기타", ProductCategory.GIRLS_CLOTHING),

    // 남아 의류
    BOYS_CLOTHING_ALL("전체", ProductCategory.BOYS_CLOTHING),
    BOYS_CLOTHING_SET("상하복 세트", ProductCategory.BOYS_CLOTHING),
    BOYS_CLOTHING_JACKET("자켓/점퍼", ProductCategory.BOYS_CLOTHING),
    BOYS_CLOTHING_COAT("코트/정장", ProductCategory.BOYS_CLOTHING),
    BOYS_CLOTHING_CARDIGAN("가디건/조끼", ProductCategory.BOYS_CLOTHING),
    BOYS_CLOTHING_KNIT("니트/스웨터", ProductCategory.BOYS_CLOTHING),
    BOYS_CLOTHING_SHIRT("셔츠/남방", ProductCategory.BOYS_CLOTHING),
    BOYS_CLOTHING_TSHIRT("티셔츠", ProductCategory.BOYS_CLOTHING),
    BOYS_CLOTHING_PANTS("바지", ProductCategory.BOYS_CLOTHING),
    BOYS_CLOTHING_SPORTS("스포츠/테마의류", ProductCategory.BOYS_CLOTHING),
    BOYS_CLOTHING_UNDERWEAR("속옷/잠옷", ProductCategory.BOYS_CLOTHING),
    BOYS_CLOTHING_ACCESSORIES("액세서리/모자/양말", ProductCategory.BOYS_CLOTHING),
    BOYS_CLOTHING_SHOES("신발", ProductCategory.BOYS_CLOTHING),
    BOYS_CLOTHING_ETC("기타", ProductCategory.BOYS_CLOTHING),

    // 유아동 용품
    BABY_PRODUCTS_ALL("전체", ProductCategory.BABY_PRODUCTS),
    BABY_PRODUCTS_STROLLER("유모차", ProductCategory.BABY_PRODUCTS),
    BABY_PRODUCTS_CARRIER("아기띠", ProductCategory.BABY_PRODUCTS),
    BABY_PRODUCTS_CARSEAT("카시트", ProductCategory.BABY_PRODUCTS),
    BABY_PRODUCTS_WALKER("보행기/쏘서", ProductCategory.BABY_PRODUCTS),
    BABY_PRODUCTS_DIAPER("기저귀", ProductCategory.BABY_PRODUCTS),
    BABY_PRODUCTS_FURNITURE("가구/침대/매트", ProductCategory.BABY_PRODUCTS),
    BABY_PRODUCTS_BEDDING("이불/침구", ProductCategory.BABY_PRODUCTS),
    BABY_PRODUCTS_BATH("목욕/구강용품", ProductCategory.BABY_PRODUCTS),
    BABY_PRODUCTS_HYGIENE("세탁/위생용품", ProductCategory.BABY_PRODUCTS),
    BABY_PRODUCTS_SKINCARE("유아동 스킨케어", ProductCategory.BABY_PRODUCTS),
    BABY_PRODUCTS_ETC("기타", ProductCategory.BABY_PRODUCTS),

    // 교구/장난감
    TOYS_EDUCATIONAL_ALL("전체", ProductCategory.TOYS_EDUCATIONAL),
    TOYS_EDUCATIONAL_DOLL("인형/소형 장난감", ProductCategory.TOYS_EDUCATIONAL),
    TOYS_EDUCATIONAL_ROBOT("로봇", ProductCategory.TOYS_EDUCATIONAL),
    TOYS_EDUCATIONAL_LEARNING("교구/CD/DVD", ProductCategory.TOYS_EDUCATIONAL),
    TOYS_EDUCATIONAL_NEWBORN("신생아 완구", ProductCategory.TOYS_EDUCATIONAL),
    TOYS_EDUCATIONAL_RIDE_ON("자전거/승용 완구", ProductCategory.TOYS_EDUCATIONAL),
    TOYS_EDUCATIONAL_WATER_PLAY("물놀이/계절용품", ProductCategory.TOYS_EDUCATIONAL),
    TOYS_EDUCATIONAL_PLAYHOUSE("놀이집/텐트/미끄럼틀", ProductCategory.TOYS_EDUCATIONAL),
    TOYS_EDUCATIONAL_ETC("기타", ProductCategory.TOYS_EDUCATIONAL),

    // 수유/이유 용품
    FEEDING_WEANING_ALL("전체", ProductCategory.FEEDING_WEANING),
    FEEDING_WEANING_NURSING("수유 용품", ProductCategory.FEEDING_WEANING),
    FEEDING_WEANING_BOTTLE("젖병/쪽쪽이", ProductCategory.FEEDING_WEANING),
    FEEDING_WEANING_FORMULA("분유", ProductCategory.FEEDING_WEANING),
    FEEDING_WEANING_BABY_FOOD("이유식 용품", ProductCategory.FEEDING_WEANING),
    FEEDING_WEANING_ETC("기타", ProductCategory.FEEDING_WEANING),

    // 엄마 용품
    MOTHER_PRODUCTS_ALL("전체", ProductCategory.MOTHER_PRODUCTS),
    MOTHER_PRODUCTS_MATERNITY("임부의류/수유복", ProductCategory.MOTHER_PRODUCTS),
    MOTHER_PRODUCTS_HEALTH("건강/미용", ProductCategory.MOTHER_PRODUCTS),
    MOTHER_PRODUCTS_ETC("기타", ProductCategory.MOTHER_PRODUCTS);

    companion object {
        fun getByParentCategory(parentCategory: ProductCategory): List<DetailedProductCategory> {
            return values().filter { it.parentCategory == parentCategory }
        }
        
        fun getAllByDisplayName(displayName: String): DetailedProductCategory? {
            return values().find { it.displayName == displayName }
        }
    }
}