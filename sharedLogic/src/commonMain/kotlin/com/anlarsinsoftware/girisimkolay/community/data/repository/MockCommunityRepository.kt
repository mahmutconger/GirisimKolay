package com.anlarsinsoftware.girisimkolay.community.data.repository

import com.anlarsinsoftware.girisimkolay.community.domain.entity.CommunityAuthorType
import com.anlarsinsoftware.girisimkolay.community.domain.entity.CommunityPost
import com.anlarsinsoftware.girisimkolay.community.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockCommunityRepository : CommunityRepository {
    override fun getNetworkPosts(): Flow<List<CommunityPost>> = flowOf(
        listOf(
            CommunityPost(
                id = "1",
                authorName = "Ahmet Yılmaz",
                content = "E-ihracat gümrük beyannamesini nasıl çözdünüz? Özellikle mikro ihracat kapsamında ETGB işlemleri çok karışık geldi.",
                likes = 12,
                commentsCount = 4,
                authorType = CommunityAuthorType.ENTREPRENEUR
            ),
            CommunityPost(
                id = "2",
                authorName = "Zeynep Kaya",
                content = "KOSGEB İş Planı sunumundan yeni çıktım. Püf noktası: Finansal tablolarınızı mutlaka yapay zeka ile önden simüle edin.",
                likes = 34,
                commentsCount = 8,
                authorType = CommunityAuthorType.ENTREPRENEUR
            )
        )
    )

    override fun getExpertPosts(): Flow<List<CommunityPost>> = flowOf(
        listOf(
            CommunityPost(
                id = "3",
                authorName = "Mali Müşavir Ayşe K.",
                content = "KOSGEB İş Planı Onayı: İş planınızı sunarken gelir-gider dengenizi reel enflasyon beklentilerine göre 3 yıllık projeksiyon ile hazırlayın. Destekler doğrudan değil, fatura bazlı ödenmektedir.",
                likes = 156,
                commentsCount = 23,
                isPinned = true,
                authorType = CommunityAuthorType.EXPERT,
                isVerifiedExpert = true
            ),
            CommunityPost(
                id = "4",
                authorName = "Hukukçu Mert A.",
                content = "Şahıs şirketi açarken 'Genç Girişimci İstisnası' şartlarını taşıyıp taşımadığınızı mutlaka kontrol edin. 3 yıl boyunca gelir vergisi muafiyeti büyük avantaj sağlar.",
                likes = 89,
                commentsCount = 11,
                authorType = CommunityAuthorType.EXPERT,
                isVerifiedExpert = true
            )
        )
    )
}
