package com.anlarsinsoftware.girisimkolay.dashboard.data.repository

import com.anlarsinsoftware.girisimkolay.dashboard.domain.entity.NewsArticle
import com.anlarsinsoftware.girisimkolay.dashboard.domain.entity.UserStatus
import com.anlarsinsoftware.girisimkolay.dashboard.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockDashboardRepository : NewsRepository {
    override fun getUserStatus(): Flow<UserStatus> = flowOf(
        UserStatus(
            name = "Mahmut Can",
            companyType = "Şahıs Şirketi",
            entrepreneurType = "Mikro İhracatçı Girişimi"
        )
    )

    override fun getNewsFeed(): Flow<List<NewsArticle>> = flowOf(
        listOf(
            NewsArticle(
                id = "1",
                title = "KOSGEB Mikro İhracat Desteği",
                source = "KOSGEB",
                summary = "2026 yılı için mikro ihracatçı girişimlere 100.000 TL'ye kadar hibe desteği onaylandı.",
                sentimentScore = 1,
                sentimentText = "Fırsat",
                sourceTag = "KOSGEB"
            ),
            NewsArticle(
                id = "2",
                title = "E-Ticaret Mevzuat Güncellemesi",
                source = "GİB",
                summary = "E-ticaret yapan şahıs şirketlerinin KDV beyanname sürelerinde değişikliğe gidildi.",
                sentimentScore = 0,
                sentimentText = "Mevzuat Değişikliği",
                sourceTag = "GİB"
            )
        )
    )
}
