package com.example.data.local

import com.example.data.model.AchievementEntity
import com.example.data.model.AnnouncementEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.ChapterEntity
import com.example.data.model.CreativeSubmissionEntity
import com.example.data.model.ExamAttemptEntity
import com.example.data.model.ExamEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.QuestionOptionEntity
import com.example.data.model.StudentAnswerEntity
import com.example.data.model.SubjectEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseInitializer {

    suspend fun populateInitialData(database: AppDatabase) = withContext(Dispatchers.IO) {
        val userDao = database.userDao()
        val eduDao = database.educationDao()
        val examDao = database.examDao()
        val creativeDao = database.creativeDao()
        val platformDao = database.platformDao()

        // 1. Initial Users
        val users = listOf(
            UserEntity(
                id = 1L,
                fullName = "Tanvir Ahmed",
                email = "tanvir@notredame.edu.bd",
                phone = "01712345678",
                passwordHash = "pass123",
                institution = "Notre Dame College, Dhaka",
                educationLevel = "HSC - Science",
                role = "STUDENT",
                points = 2480,
                isPhoneVerified = true,
                isEmailVerified = true
            ),
            UserEntity(
                id = 2L,
                fullName = "Suborna Akter",
                email = "suborna@vnc.edu.bd",
                phone = "01812345679",
                institution = "Viqarunnisa Noon College",
                educationLevel = "HSC - Science",
                role = "STUDENT",
                points = 2850
            ),
            UserEntity(
                id = 3L,
                fullName = "Fahim Morshed",
                email = "fahim@dhakacollege.edu.bd",
                phone = "01912345680",
                institution = "Dhaka College",
                educationLevel = "HSC - Science",
                role = "STUDENT",
                points = 2620
            ),
            UserEntity(
                id = 4L,
                fullName = "Nusrat Jahan",
                email = "nusrat@rajuk.edu.bd",
                phone = "01612345681",
                institution = "Rajuk Uttara Model College",
                educationLevel = "HSC - Business Studies",
                role = "STUDENT",
                points = 2390
            ),
            UserEntity(
                id = 5L,
                fullName = "Arafat Hossain",
                email = "arafat@ctgcollege.edu.bd",
                phone = "01512345682",
                institution = "Chittagong College",
                educationLevel = "HSC - Science",
                role = "STUDENT",
                points = 2150
            ),
            UserEntity(
                id = 99L,
                fullName = "Prof. M. Rahman (Admin)",
                email = "admin@edurank.bd",
                phone = "01700000000",
                passwordHash = "admin123",
                institution = "EduRank Academic Central Board",
                educationLevel = "Academic Admin",
                role = "ADMIN",
                points = 9999
            )
        )
        userDao.insertUsers(users)

        // 2. Categories
        val categories = listOf(
            CategoryEntity("HSC", "HSC (Higher Secondary)", "Science, Business Studies & Humanities curriculum", "school", 1),
            CategoryEntity("SSC", "SSC (Secondary Certificate)", "Class 9-10 Board preparation curriculum", "auto_stories", 2),
            CategoryEntity("ALIM", "Alim (Madrasah Higher Secondary)", "Quran, Hadith, Fiqh and general sciences", "mosque", 3),
            CategoryEntity("DAKHIL", "Dakhil (Madrasah Secondary)", "Islamic studies with core board curriculum", "menu_book", 4),
            CategoryEntity("JSC", "JSC / Junior Level", "Foundational junior board preparation", "child_care", 5),
            CategoryEntity("OTHER", "Admission & Olympiads", "Medical, BUET & University admission tests", "military_tech", 6)
        )
        eduDao.insertCategories(categories)

        // 3. Subjects
        val subjects = listOf(
            // HSC Science
            SubjectEntity("hsc_phy_1", "HSC", "Science", "Physics 1st Paper", "PHY-101", 10, "#4F46E5"),
            SubjectEntity("hsc_phy_2", "HSC", "Science", "Physics 2nd Paper", "PHY-102", 10, "#4338CA"),
            SubjectEntity("hsc_chem_1", "HSC", "Science", "Chemistry 1st Paper", "CHEM-101", 5, "#059669"),
            SubjectEntity("hsc_chem_2", "HSC", "Science", "Chemistry 2nd Paper", "CHEM-102", 5, "#10B981"),
            SubjectEntity("hsc_bio_1", "HSC", "Science", "Biology 1st Paper (Botany)", "BIO-101", 12, "#16A34A"),
            SubjectEntity("hsc_bio_2", "HSC", "Science", "Biology 2nd Paper (Zoology)", "BIO-102", 12, "#15803D"),
            SubjectEntity("hsc_math_1", "HSC", "Science", "Higher Math 1st Paper", "MATH-101", 10, "#D97706"),
            SubjectEntity("hsc_math_2", "HSC", "Science", "Higher Math 2nd Paper", "MATH-102", 10, "#B45309"),
            SubjectEntity("hsc_ict", "HSC", "Science", "ICT (Information & Communication)", "ICT-101", 6, "#6345ED"),

            // HSC Business Studies
            SubjectEntity("hsc_acc", "HSC", "Business Studies", "Accounting 1st & 2nd Paper", "ACC-201", 8, "#0284C7"),
            SubjectEntity("hsc_bom", "HSC", "Business Studies", "Business Organization & Management", "BOM-202", 8, "#2563EB"),
            SubjectEntity("hsc_fin", "HSC", "Business Studies", "Finance, Banking & Insurance", "FIN-203", 8, "#7C3AED"),
            SubjectEntity("hsc_ict_bs", "HSC", "Business Studies", "ICT (Information & Communication)", "ICT-101", 6, "#6345ED"),

            // HSC Humanities
            SubjectEntity("hsc_hist", "HSC", "Humanities", "History of Bangladesh & World", "HIST-301", 8, "#DC2626"),
            SubjectEntity("hsc_geo", "HSC", "Humanities", "Geography & Environment", "GEO-302", 8, "#EA580C"),
            SubjectEntity("hsc_civ", "HSC", "Humanities", "Civics & Good Governance", "CIV-303", 8, "#9333EA"),
            SubjectEntity("hsc_econ", "HSC", "Humanities", "Economics 1st & 2nd Paper", "ECON-304", 10, "#0D9488"),
            SubjectEntity("hsc_logic", "HSC", "Humanities", "Logic 1st & 2nd Paper", "LOGIC-305", 8, "#4B5563"),
            SubjectEntity("hsc_ict_hum", "HSC", "Humanities", "ICT (Information & Communication)", "ICT-101", 6, "#6345ED"),

            // SSC
            SubjectEntity("ssc_math", "SSC", "General", "General Mathematics", "SSC-MATH", 17, "#D97706"),
            SubjectEntity("ssc_phy", "SSC", "Science", "SSC Physics", "SSC-PHY", 14, "#4F46E5"),
            SubjectEntity("ssc_chem", "SSC", "Science", "SSC Chemistry", "SSC-CHEM", 12, "#059669"),
            SubjectEntity("ssc_bio", "SSC", "Science", "SSC Biology", "SSC-BIO", 14, "#16A34A"),
            SubjectEntity("ssc_ict", "SSC", "General", "SSC ICT", "SSC-ICT", 6, "#6345ED"),

            // ALIM
            SubjectEntity("alim_quran", "ALIM", "General", "Quran Majid & Hadith", "ALIM-QURAN", 10, "#059669"),
            SubjectEntity("alim_fiqh", "ALIM", "General", "Fiqh 1st & 2nd Paper", "ALIM-FIQH", 8, "#0D9488"),
            SubjectEntity("alim_arabic", "ALIM", "General", "Arabic 1st & 2nd Paper", "ALIM-ARABIC", 8, "#2563EB")
        )
        eduDao.insertSubjects(subjects)

        // 4. Chapters for Physics 1st Paper
        val chapters = listOf(
            ChapterEntity("ch_phy1_1", "hsc_phy_1", 1, "ভৌত জগৎ ও পরিমাপ (Physical World & Measurement)", 20),
            ChapterEntity("ch_phy1_2", "hsc_phy_1", 2, "ভেক্টর (Vector)", 25),
            ChapterEntity("ch_phy1_3", "hsc_phy_1", 3, "গতিবিদ্যা (Dynamics)", 25),
            ChapterEntity("ch_phy1_4", "hsc_phy_1", 4, "নিউটনিয়ান বলবিদ্যা (Newtonian Mechanics)", 25),
            ChapterEntity("ch_phy1_5", "hsc_phy_1", 5, "কাজ, শক্তি ও ক্ষমতা (Work, Energy & Power)", 20),
            ChapterEntity("ch_phy1_6", "hsc_phy_1", 6, "মহাকর্ষ ও অভিকর্ষ (Gravitation & Gravity)", 25),
            ChapterEntity("ch_phy1_7", "hsc_phy_1", 7, "পদার্থের গাঠনিক ধর্ম (Structural Properties of Matter)", 20),
            ChapterEntity("ch_phy1_8", "hsc_phy_1", 8, "পর্যায়বৃত্ত গতি (Periodic Motion)", 25),
            ChapterEntity("ch_phy1_9", "hsc_phy_1", 9, "তরঙ্গ (Waves)", 20),
            ChapterEntity("ch_phy1_10", "hsc_phy_1", 10, "আদর্শ গ্যাস ও গ্যাসের গতিতত্ত্ব (Ideal Gas & Kinetics)", 25)
        )
        eduDao.insertChapters(chapters)

        // 5. Exams
        val exams = listOf(
            ExamEntity(
                id = "exam_phy1_mcq_board",
                title = "HSC Physics 1st Paper - Full Board Standard Model Test",
                subjectId = "hsc_phy_1",
                categoryId = "HSC",
                examType = "MCQ",
                groupName = "Science",
                totalQuestions = 25,
                durationMinutes = 25,
                totalMarks = 25f,
                passMarks = 10f,
                negativeMarkingPerWrong = 0.25f,
                isPublished = true
            ),
            ExamEntity(
                id = "exam_phy1_creative_1",
                title = "HSC Physics 1st Paper - Creative (সৃজনশীল) Model Exam 01",
                subjectId = "hsc_phy_1",
                categoryId = "HSC",
                examType = "CREATIVE",
                groupName = "Science",
                totalQuestions = 1,
                durationMinutes = 45,
                totalMarks = 10f,
                passMarks = 4f,
                negativeMarkingPerWrong = 0f,
                isPublished = true
            ),
            ExamEntity(
                id = "exam_chem1_mcq_1",
                title = "HSC Chemistry 1st Paper - Periodic Properties & Chemical Bonds",
                subjectId = "hsc_chem_1",
                categoryId = "HSC",
                examType = "MCQ",
                groupName = "Science",
                totalQuestions = 25,
                durationMinutes = 25,
                totalMarks = 25f,
                passMarks = 10f,
                negativeMarkingPerWrong = 0.25f,
                isPublished = true
            ),
            ExamEntity(
                id = "exam_ict_mcq_1",
                title = "HSC ICT - Digital Devices & Logic Gates Rapid Test",
                subjectId = "hsc_ict",
                categoryId = "HSC",
                examType = "MCQ",
                groupName = "Science",
                totalQuestions = 25,
                durationMinutes = 25,
                totalMarks = 25f,
                passMarks = 10f,
                negativeMarkingPerWrong = 0.25f,
                isPublished = true
            ),
            ExamEntity(
                id = "exam_acc_mcq_1",
                title = "HSC Accounting - Financial Statements & Ledger Model Test",
                subjectId = "hsc_acc",
                categoryId = "HSC",
                examType = "MCQ",
                groupName = "Business Studies",
                totalQuestions = 30,
                durationMinutes = 30,
                totalMarks = 30f,
                passMarks = 12f,
                negativeMarkingPerWrong = 0.25f,
                isPublished = true
            ),
            ExamEntity(
                id = "exam_ssc_math_mcq_1",
                title = "SSC General Math - Algebraic Expressions & Geometry Practice",
                subjectId = "ssc_math",
                categoryId = "SSC",
                examType = "MCQ",
                groupName = "General",
                totalQuestions = 30,
                durationMinutes = 30,
                totalMarks = 30f,
                passMarks = 12f,
                negativeMarkingPerWrong = 0.25f,
                isPublished = true
            )
        )
        examDao.insertExams(exams)

        // 6. Questions & Options for Physics 1st Paper MCQ
        val questions = mutableListOf<QuestionEntity>()
        val options = mutableListOf<QuestionOptionEntity>()

        // 25 realistic HSC Physics Questions
        val qData = listOf(
            Triple(
                "দুটি সমমানের ভেক্টর কোনো বিন্দুতে ক্রিয়াশীল হলে এদের লব্ধির মান যেকোনো একটি ভেক্টরের মানের সমান হতে হলে মধ্যবর্তী কোণ কত হবে?",
                listOf("0°", "60°", "90°", "120°"),
                3 to "R = 2P cos(θ/2)। যদি R = P হয়, তবে cos(θ/2) = 1/2 ⇒ θ/2 = 60° ⇒ θ = 120°।"
            ),
            Triple(
                "কোনো বস্তুর বেগ দ্বিগুণ করা হলে তার গতিশক্তি পূর্বের কত গুণ বৃদ্ধি পাবে?",
                listOf("২ গুণ", "৩ গুণ", "৪ গুণ", "৮ গুণ"),
                1 to "গতিশক্তি Ek ∝ v²। বেগ দ্বিগুণ হলে গতিশক্তি ৪ গুণ হয়, সুতরাং বৃদ্ধি পায় (৪ - ১) = ৩ গুণ।"
            ),
            Triple(
                "মহাকর্ষীয় ধ্রুবক G এর মাত্রা সমীকরণ কোনটি?",
                listOf("[M⁻¹ L³ T⁻²]", "[M L² T⁻²]", "[M⁻² L³ T⁻¹]", "[M L³ T⁻²]"),
                0 to "F = G m₁m₂ / r² থেকে G = F r² / (m₁m₂) ⇒ মাত্রা: [M L T⁻²][L²] / [M²] = [M⁻¹ L³ T⁻²]।"
            ),
            Triple(
                "নিচের কোন বলটি সংরক্ষণশীল বল (Conservative Force)?",
                listOf("ঘর্ষণ বল", "সান্দ্র বল", "অভিকর্ষ বল", "বাতাসের বাধা"),
                2 to "যে বল দ্বারা কৃতকাজ শুধুমাত্র আদি ও শেষ অবস্থানের উপর নির্ভর করে, তাকে সংরক্ষণশীল বল বলে। যেমন: অভিকর্ষ বল।"
            ),
            Triple(
                "সরল দোলকের দোলনকাল ৪ গুণ করতে হলে এর কার্যকরী দৈর্ঘ্য কত গুণ করতে হবে?",
                listOf("২ গুণ", "৪ গুণ", "৮ গুণ", "১৬ গুণ"),
                3 to "T = 2π √(L/g) ⇒ T ∝ √L ⇒ L ∝ T²। অতএব দোলনকাল ৪ গুণ হলে দৈর্ঘ্য (৪)² = ১৬ গুণ করতে হবে।"
            ),
            Triple(
                "একক ভেক্টর i, j, k এর জন্য কোনটি সঠিক?",
                listOf("i × i = 1", "i · j = 1", "i × j = k", "j × i = k"),
                2 to "ডানহাতি নিয়ম অনুসারে i × j = k, j × k = i, k × i = j।"
            ),
            Triple(
                "কৌণিক ভরবেগের একক কোনটি?",
                listOf("kg·m/s", "kg·m²/s", "N·s/m", "J·s⁻¹"),
                1 to "L = Iω অথবা L = r × p ⇒ একক: m × kg·m/s = kg·m²/s বা J·s।"
            ),
            Triple(
                "পৃষ্ঠটানের মাত্রা কোনটি?",
                listOf("[M T⁻²]", "[M L⁻¹ T⁻²]", "[M L² T⁻¹]", "[M L T⁻²]"),
                0 to "T = F / L ⇒ মাত্রা: [M L T⁻²] / [L] = [M T⁻²]।"
            ),
            Triple(
                "একটি আদর্শ গ্যাসের ক্ষেত্রে রুদ্ধতাপীয় প্রক্রিয়ায় কোনটি ধ্রুবক থাকে?",
                listOf("তাপমাত্রা (T)", "চাপ (P)", "এনট্রপি (Entropy)", "আয়তন (V)"),
                2 to "রুদ্ধতাপীয় পরিবর্তনে বাইরে থেকে তাপ প্রবেশ করে না বা বের হয় না (dQ = 0), তাই এনট্রপি স্থির থাকে।"
            ),
            Triple(
                "ঘড়ির মিনিটের কাঁটার কৌণিক বেগ কত rad/s?",
                listOf("π / 1800", "π / 3600", "π / 7200", "π / 60"),
                0 to "ω = 2π / T। মিনিটের কাঁটার জন্য T = 60 মিনিট = 3600 s। অতএব ω = 2π / 3600 = π / 1800 rad/s।"
            ),
            Triple(
                "শব্দ তরঙ্গের ক্ষেত্রে কোনটি সত্য নয়?",
                listOf("শব্দ একটি অনুদৈর্ঘ্য তরঙ্গ", "শব্দ বিস্তারের জন্য মাধ্যমের প্রয়োজন", "শব্দ তরঙ্গে সমবর্তন (Polarization) ঘটে", "শব্দ তরঙ্গের প্রতিফলন ঘটে"),
                2 to "শব্দ তরঙ্গ অনুদৈর্ঘ্য বা যান্ত্রিক তরঙ্গ হওয়ায় এতে সমবর্তন ঘটে না। সমবর্তন শুধুমাত্র অনুপ্রস্থ বা তাড়িতচৌম্বক তরঙ্গে ঘটে।"
            ),
            Triple(
                "পরম শূন্য তাপমাত্রায় গ্যাসের গতিশক্তি কত?",
                listOf("অসীম", "শূন্য", "স্বাভাবিকের দ্বিগুণ", "১০০ জুল"),
                1 to "Ek = (3/2)kT। T = 0 K হলে গতিশক্তি শূন্য হয়ে যায়।"
            ),
            Triple(
                "একটি কৃত্রিম উপগ্রহের প্রদক্ষিণ বেগ v = √(GM/R)। উপগ্রহটির ভর দ্বিগুণ করা হলে প্রদক্ষিণ বেগ কত হবে?",
                listOf("অর্ধেক", "দ্বিগুণ", "একই থাকবে", "চারগুণ"),
                2 to "কৃত্রিম উপগ্রহের মুক্তিবেগ বা প্রদক্ষিণ বেগ উপগ্রহের নিজের ভরের উপর নির্ভর করে না।"
            ),
            Triple(
                "টর্কের (Torque) মাত্রা সমীকরণ কোনটির অনুরূপ?",
                listOf("বল", "কাজের", "কৌণিক ভরবেগ", "ক্ষমতা"),
                1 to "টর্ক τ = r × F ⇒ মাত্রা [M L² T⁻²], যা কাজ বা শক্তির মাত্রার সমান।"
            ),
            Triple(
                "পয়সনের অনুপাতের (Poisson's Ratio) তাত্ত্বিক মান কোন সীমার মধ্যে থাকে?",
                listOf("০ থেকে ১", "-১ থেকে ০.৫", "-০.৫ থেকে ০.৫", "০ থেকে ০.৫"),
                1 to "পয়সনের অনুপাত σ এর তাত্ত্বিক সীমা -১ < σ < ০.৫ এবং ব্যবহারিক সীমা ০ < σ < ০.৫।"
            ),
            Triple(
                "কেন্দ্রমুখী বল দ্বারা কৃতকাজ কত?",
                listOf("ধনাত্মক", "ঋণাত্মক", "সর্বদা শূন্য", "গতিশক্তির সমান"),
                2 to "কেন্দ্রমুখী বল সর্বদা সরণের লম্ব বরাবর ক্রিয়া করে (θ = 90°)। W = F s cos(90°) = 0।"
            ),
            Triple(
                "কোন তরঙ্গের দুটি বিন্দুর মধ্যে পথ পার্থক্য λ/4 হলে দশা পার্থক্য কত?",
                listOf("π / 4", "π / 2", "π", "2π"),
                1 to "দশা পার্থক্য δ = (2π / λ) × পথ পার্থক্য = (2π / λ) × (λ / 4) = π/2।"
            ),
            Triple(
                "সান্দ্রতাঙ্কের SI একক কোনটি?",
                listOf("Pa·s", "N/m²", "Poise", "J/s"),
                0 to "সান্দ্রতাঙ্কের SI একক হলো Pascal-second (Pa·s) বা N·s/m²।"
            ),
            Triple(
                "সরল ছন্দিত স্পন্দনে স্পন্দিত কণার সাম্যাবস্থানে কোনটির মান সর্বোচ্চ হয়?",
                listOf("বিভব শক্তি", "ত্বরণ", "বেগ", "প্রত্যয়নী বল"),
                2 to "সাম্যাবস্থানে সরণ x = 0 হওয়ায় বেগ v = ω√(A² - x²) = ωA (সর্বোচ্চ), কিন্তু ত্বরণ ও বিভব শক্তি শূন্য।"
            ),
            Triple(
                "পৃথিবীপৃষ্ঠ হতে কত উচ্চতায় অভিকর্ষজ ত্বরণের মান ভূপৃষ্ঠের মানের এক চতুর্থাংশ (g/4) হবে?",
                listOf("R/2", "R", "2R", "4R"),
                1 to "g' = g (R / (R + h))² ⇒ 1/4 = (R / (R + h))² ⇒ R/(R + h) = 1/2 ⇒ R + h = 2R ⇒ h = R।"
            ),
            Triple(
                "বায়ুর আর্দ্রতা বৃদ্ধি পেলে শব্দের বেগের কী পরিবর্তন ঘটে?",
                listOf("হ্রাস পায়", "বৃদ্ধি পায়", "অপরিবর্তিত থাকে", "শূন্য হয়"),
                1 to "জলীয় বাষ্পের ঘনত্ব শুষ্ক বায়ুর ঘনত্বের চেয়ে কম। ঘনত্ব হ্রাস পাওয়ায় আর্দ্র বায়ুতে শব্দের বেগ বৃদ্ধি পায়।"
            ),
            Triple(
                "স্থির তরঙ্গে পরপর দুটি নিস্পন্দ বিন্দুর মধ্যবর্তী দূরত্ব কত?",
                listOf("λ", "λ / 2", "λ / 4", "2λ"),
                1 to "স্থির তরঙ্গে দুটি পরপর নিস্পন্দ বিন্দু বা দুটি সুস্পন্দ বিন্দুর মধ্যবর্তী দূরত্ব হলো λ/2।"
            ),
            Triple(
                "১ অশ্বক্ষমতা (1 HP) সমান কত ওয়াট?",
                listOf("৫০০ ওয়াট", "৭৪৬ ওয়াট", "১০০০ ওয়াট", "৩৬০০ ওয়াট"),
                1 to "১ হর্সপাওয়ার (1 HP) = ৭৪৬ ওয়াট।"
            ),
            Triple(
                "প্রক্ষেপকের (Projectile) সর্বোচ্চ উচ্চতায় বেগ ও ত্বরণের মধ্যবর্তী কোণ কত?",
                listOf("0°", "45°", "90°", "180°"),
                2 to "সর্বোচ্চ উচ্চতায় উলম্ব বেগ শূন্য হয়ে যায়, কেবল অনুভূমিক বেগ থাকে। ত্বরণ g নিচের দিকে কাজ করায় মধ্যবর্তী কোণ 90°।"
            ),
            Triple(
                "গ্যাসের মূল গড় বর্গবেগ (Crms) তাপমাত্রার সাথে কীভাবে পরিবর্তিত হয়?",
                listOf("Crms ∝ T", "Crms ∝ √T", "Crms ∝ T²", "Crms ∝ 1/√T"),
                1 to "Crms = √(3RT/M) ⇒ Crms ∝ √T (পরম তাপমাত্রার বর্গমূলের সমানুপাতিক)।"
            )
        )

        qData.forEachIndexed { index, (qText, optList, correctPair) ->
            val qId = "q_phy1_${index + 1}"
            questions.add(
                QuestionEntity(
                    id = qId,
                    examId = "exam_phy1_mcq_board",
                    subjectId = "hsc_phy_1",
                    questionType = "MCQ",
                    questionNumber = index + 1,
                    questionText = qText,
                    explanation = correctPair.second,
                    marks = 1.0f
                )
            )
            optList.forEachIndexed { optIdx, optText ->
                options.add(
                    QuestionOptionEntity(
                        id = "${qId}_opt_$optIdx",
                        questionId = qId,
                        optionIndex = optIdx,
                        optionText = optText,
                        isCorrect = (optIdx == correctPair.first)
                    )
                )
            }
        }
        examDao.insertQuestions(questions)
        examDao.insertOptions(options)

        // 7. Creative Question for HSC Physics
        val creativeQ = QuestionEntity(
            id = "q_creative_phy1_1",
            examId = "exam_phy1_creative_1",
            subjectId = "hsc_phy_1",
            questionType = "CREATIVE",
            questionNumber = 1,
            questionText = "নিউটনীয় বলবিদ্যা ও কৌণিক গতি সংক্রান্ত সৃজনশীল প্রশ্ন",
            stimulus = "উদ্দীপক: ৫০ কেজি ভরের একজন বালক এবং ৬০ কেজি ভরের একজন তরুণ যথাক্রমে ২০ মিটার এবং ২৫ মিটার ব্যাসার্ধের দুটি বৃত্তাকার ট্র্যাকে একই রৈখিক দ্রুতিতে সাইকেল চালাচ্ছে। তাদের সাইকেলের টায়ার ও রাস্তার মধ্যকার ঘর্ষণ গুণাঙ্ক ০.৩। ট্র্যাক দুটিতে কোনো ব্যাংকিং নেই।",
            marks = 10f,
            partA = "ক) জড়তার ভ্রামক কাকে বলে? (১ নম্বর)",
            partB = "খ) ঘূর্ণনরত কোনো বস্তুর ব্যাসার্ধ অর্ধেক করা হলে তার কৌণিক বেগ কীভাবে পরিবর্তিত হবে ব্যাখ্যা করো। (২ নম্বর)",
            partC = "গ) বালকটির ট্র্যাকে না পিছলিয়ে নিরাপদে ঘোরার জন্য সর্বোচ্চ দ্রুতি নির্ণয় করো। (৩ নম্বর)",
            partD = "ঘ) উভয় সাইকেল আরোহী একই দ্রুতিতে মোড় নিতে চাইলে তরুণটির ছিটকে পড়ার সম্ভাবনা তুলনামূলকভাবে বেশি কি না— গাণিতিকভাবে বিশ্লেষণ করো। (৪ নম্বর)"
        )
        examDao.insertQuestion(creativeQ)

        // 8. Past attempts for Tanvir (User 1) to populate realistic Dashboard statistics
        val pastAttempt1 = ExamAttemptEntity(
            id = "attempt_101",
            userId = 1L,
            examId = "exam_phy1_mcq_board",
            examTitle = "HSC Physics 1st Paper - Full Board Standard Model Test",
            subjectName = "Physics 1st Paper",
            examType = "MCQ",
            startTime = System.currentTimeMillis() - 86400000L * 2,
            endTime = System.currentTimeMillis() - 86400000L * 2 + 1200000L,
            timeTakenSeconds = 1140L,
            totalQuestions = 25,
            answeredCount = 25,
            correctCount = 22,
            wrongCount = 3,
            unattemptedCount = 0,
            score = 21.25f, // 22 - (3 * 0.25)
            totalMarks = 25f,
            accuracyPercentage = 88.0f,
            isPassed = true,
            status = "SUBMITTED"
        )
        val pastAttempt2 = ExamAttemptEntity(
            id = "attempt_102",
            userId = 1L,
            examId = "exam_chem1_mcq_1",
            examTitle = "HSC Chemistry 1st Paper - Periodic Properties",
            subjectName = "Chemistry 1st Paper",
            examType = "MCQ",
            startTime = System.currentTimeMillis() - 86400000L * 4,
            endTime = System.currentTimeMillis() - 86400000L * 4 + 1100000L,
            timeTakenSeconds = 1050L,
            totalQuestions = 25,
            answeredCount = 24,
            correctCount = 20,
            wrongCount = 4,
            unattemptedCount = 1,
            score = 19.0f,
            totalMarks = 25f,
            accuracyPercentage = 83.3f,
            isPassed = true,
            status = "SUBMITTED"
        )
        val pastAttempt3 = ExamAttemptEntity(
            id = "attempt_103",
            userId = 1L,
            examId = "exam_ict_mcq_1",
            examTitle = "HSC ICT - Digital Devices & Logic Gates Rapid Test",
            subjectName = "ICT",
            examType = "MCQ",
            startTime = System.currentTimeMillis() - 86400000L * 6,
            endTime = System.currentTimeMillis() - 86400000L * 6 + 950000L,
            timeTakenSeconds = 920L,
            totalQuestions = 25,
            answeredCount = 25,
            correctCount = 24,
            wrongCount = 1,
            unattemptedCount = 0,
            score = 23.75f,
            totalMarks = 25f,
            accuracyPercentage = 96.0f,
            isPassed = true,
            status = "SUBMITTED"
        )
        examDao.insertAttempt(pastAttempt1)
        examDao.insertAttempt(pastAttempt2)
        examDao.insertAttempt(pastAttempt3)

        // Seed sample answers for pastAttempt1 so review works immediately
        val sampleStudentAnswers = (1..25).map { idx ->
            val qId = "q_phy1_$idx"
            val isWrong = (idx == 2 || idx == 7 || idx == 15)
            val selected = if (isWrong) 0 else (qData[idx - 1].third.first)
            StudentAnswerEntity(
                id = "sa_101_$idx",
                attemptId = "attempt_101",
                questionId = qId,
                selectedOptionIndex = selected,
                isMarkedForReview = false,
                isCorrect = !isWrong,
                marksObtained = if (!isWrong) 1.0f else -0.25f
            )
        }
        examDao.insertStudentAnswers(sampleStudentAnswers)

        // 9. Creative Submission sample evaluated by teacher
        val sampleCreativeSub = CreativeSubmissionEntity(
            id = "c_sub_201",
            attemptId = "att_c_201",
            examId = "exam_phy1_creative_1",
            questionId = "q_creative_phy1_1",
            userId = 1L,
            studentName = "Tanvir Ahmed",
            examTitle = "HSC Physics 1st Paper - Creative Model Exam 01",
            subjectName = "Physics 1st Paper",
            answerA = "নির্দিষ্ট অক্ষের সাপেক্ষে ঘূর্ণায়মান কণার ভর এবং অক্ষ থেকে দূরত্বের বর্গের গুণফলকে জড়তার ভ্রামক বলে। I = mr²।",
            answerB = "কৌণিক ভরবেগ সংরক্ষণশীলতা নীতি অনুযায়ী L = Iω = ধ্রুবক। ব্যাসার্ধ অর্ধেক হলে I = mr² চারগুণ হ্রাস পায়। ফলে কৌণিক ভরবেগ ঠিক রাখতে কৌণিক বেগ ω চারগুণ বৃদ্ধি পাবে।",
            answerC = "আমরা জানি, না পিছলিয়ে নিরাপদে ঘোরার জন্য সর্বোচ্চ বেগ vmax = √(μ·r·g)। এখানে μ = ০.৩, r = ২০ মি, g = ৯.৮ মি/সে²। vmax = √(০.৩ × ২০ × ৯.৮) = ৭.৬৬৮ মি/সে।",
            answerD = "উভয় আরোহীর ক্ষেত্রে পিছলানোর শর্ত ভরের উপর নির্ভরশীল নয়, বরং ব্যাসার্ধের সাথে সম্পর্কিত। তরুণের ব্যাসার্ধ r₂ = ২৫ মি, যা বালকের r₁ = ২০ মি অপেক্ষা বেশি। vmax₂ = √(০.৩ × ২৫ × ৯.৮) = ৮.৫৭ মি/সে। যেহেতু তরুণের সর্বোচ্চ নিরাপদ গতিসীমা বেশি, তাই একই দ্রুতিতে ঘোরার সময় বালকের ছিটকে পড়ার ঝুঁকি বেশি হবে, তরুণের নয়।",
            marksA = 1.0f,
            marksB = 2.0f,
            marksC = 3.0f,
            marksD = 3.5f,
            totalAwardedMarks = 9.5f,
            teacherFeedback = "চমৎকার এবং সুবিন্যস্ত উপস্থাপন! 'ঘ' অংশে ব্যাখ্যাটি যথাযথ ও নির্ভুল হয়েছে। ফুল মার্কস ৯.৫/১০।",
            status = "EVALUATED"
        )
        creativeDao.insertSubmission(sampleCreativeSub)

        // 10. Platform Announcements & Achievements
        val announcements = listOf(
            AnnouncementEntity(
                id = "ann_1",
                title = "HSC Board Standard Mega Mock Test Live Now!",
                content = "All HSC 2026 examinees can participate in the nationwide Physics & Chemistry mock exam. Compete with students from NDC, Dhaka College, VNC and win medals on the National EduRank Leaderboard.",
                category = "Exam Alert"
            ),
            AnnouncementEntity(
                id = "ann_2",
                title = "Creative (সৃজনশীল) Answer Evaluation by Senior College Lecturers",
                content = "Written answers submitted in Creative Exams are now evaluated within 24 hours with step-by-step scoring (ক, খ, গ, ঘ) and detailed teacher remarks.",
                category = "Curriculum"
            )
        )
        platformDao.insertAnnouncements(announcements)

        val achievements = listOf(
            AchievementEntity("ach_1", "First Exam Finisher", "Completed first board standard exam", "star", 100, true),
            AchievementEntity("ach_2", "Accuracy Master", "Achieved over 90% accuracy in an exam", "military_tech", 250, true),
            AchievementEntity("ach_3", "Physics Prodigy", "Scored 20+ in Physics 1st Paper", "bolt", 200, true),
            AchievementEntity("ach_4", "Creative Writer", "Submitted a Creative (সৃজনশীল) paper", "edit_note", 150, true),
            AchievementEntity("ach_5", "Top 10 Nationwide", "Reach top 10 on the EduRank Leaderboard", "emoji_events", 500, false),
            AchievementEntity("ach_6", "Streak Champion", "Take exams 7 days in a row", "local_fire_department", 300, false)
        )
        platformDao.insertAchievements(achievements)
    }
}
