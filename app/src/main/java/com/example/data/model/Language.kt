package com.example.data.model

data class Language(
    val code: String,
    val name: String,
    val nativeName: String,
    val greeting: String,
    val welcomeMessage: String,
    val startButtonText: String,
    val consentExplanation: String
) {
    companion object {
        val SUPPORTED_LANGUAGES = listOf(
            Language(
                code = "en",
                name = "English",
                nativeName = "English",
                greeting = "Welcome to City Care Hospital",
                welcomeMessage = "Please select your language to begin your digital check-in.",
                startButtonText = "Get Started",
                consentExplanation = "I consent to the collection and clinical processing of my health information to prepare my intake for the doctor."
            ),
            Language(
                code = "hi",
                name = "Hindi",
                nativeName = "हिन्दी",
                greeting = "सिटी केयर अस्पताल में आपका स्वागत है",
                welcomeMessage = "डिजिटल चेक-इन शुरू करने के लिए कृपया अपनी भाषा चुनें।",
                startButtonText = "शुरू करें",
                consentExplanation = "मैं डॉक्टर के लिए स्वास्थ्य जानकारी एकत्र करने और क्लिनिकल प्रोसेसिंग के लिए सहमति देता/देती हूँ।"
            ),
            Language(
                code = "ta",
                name = "Tamil",
                nativeName = "தமிழ்",
                greeting = "சிட்டி கேர் மருத்துவமனைக்கு வருக",
                welcomeMessage = "உங்கள் டிஜிட்டல் சரிபார்ப்பைத் தொடங்க உங்கள் மொழியைத் தேர்ந்தெடுக்கவும்.",
                startButtonText = "தொடங்குங்கள்",
                consentExplanation = "மருத்துவரிடம் காண்பிப்பதற்காக எனது சுகாதாரத் தகவல்களைச் சேகரித்து செயலாக்க ஒப்புக்கொள்கிறேன்."
            ),
            Language(
                code = "te",
                name = "Telugu",
                nativeName = "తెలుగు",
                greeting = "సిటీ కేర్ హాస్పిటల్‌కు స్వాగతం",
                welcomeMessage = "మీ డిజిటల్ చెక్-ఇన్ ప్రారంభించడానికి దయచేసి మీ భాషను ఎంచుకోండి.",
                startButtonText = "ప్రారంభించండి",
                consentExplanation = "డాక్టర్ కోసం నా ఆరోగ్య సమాచారాన్ని సేకరించడానికి మరియు ప్రాసెస్ చేయడానికి నేను అంగీకరిస్తున్నాను."
            ),
            Language(
                code = "bn",
                name = "Bengali",
                nativeName = "বাংলা",
                greeting = "সিটি কেয়ার হাসপাতালে স্বাগতম",
                welcomeMessage = "আপনার ডিজিটাল চেক-ইন শুরু করতে অনুগ্রহ করে আপনার ভাষা নির্বাচন করুন।",
                startButtonText = "শুরু করুন",
                consentExplanation = "ডাক্তারের মূল্যায়নের জন্য আমার স্বাস্থ্য সংক্রান্ত তথ্য সংগ্রহ এবং প্রক্রিয়াকরণে সম্মতি দিচ্ছি।"
            ),
            Language(
                code = "mr",
                name = "Marathi",
                nativeName = "मराठी",
                greeting = "सिटी केअर हॉस्पिटलमध्ये आपले स्वागत आहे",
                welcomeMessage = "आपले डिजिटल चेक-इन सुरू करण्यासाठी कृपया भाषा निवडा.",
                startButtonText = "सुरू करा",
                consentExplanation = "डॉक्टरांच्या तपासणीसाठी माझी आरोग्य माहिती गोळा आणि प्रक्रिया करण्यास माझी संमती आहे."
            )
        )

        val DEFAULT = SUPPORTED_LANGUAGES[0]
    }
}
