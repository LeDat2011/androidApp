// Firebase configuration
const firebaseConfig = {
    apiKey: "AIzaSyBnxscyHVdl6ITnu-HGglkTY1N4lMxbcAI",
    authDomain: "composeapp-e3ab5.firebaseapp.com",
    databaseURL: "https://composeapp-e3ab5-default-rtdb.firebaseio.com",
    projectId: "composeapp-e3ab5",
    storageBucket: "composeapp-e3ab5.firebasestorage.app",
    messagingSenderId: "218582523533",
    appId: "1:218582523533:web:84a2c5b6db2bd5a5a60cc4",
    measurementId: "G-L6DSBSS484"
};

// Khởi tạo Firebase
firebase.initializeApp(firebaseConfig);

// Khởi tạo các dịch vụ Firebase
const auth = firebase.auth();
const database = firebase.database();

// Đường dẫn đến các node trong Realtime Database
const DB_PATHS = {
    VOCABULARY: 'app_data/vocabulary',
    CATEGORIES: 'app_data/categories',
    LESSONS: 'app_data/lessons',
    ACHIEVEMENTS: 'app_data/achievements',
    QUIZ: 'quizzes',
    USERS: 'app_data/users',
    SYSTEM_SETTINGS: 'app_data/system_settings'
}; 