// DOM Elements
const connectionStatus = document.getElementById('connection-status');
const connectionIcon = document.getElementById('connection-icon');
const userStatus = document.getElementById('user-status');
const pageTitle = document.getElementById('page-title');
const pageSubtitle = document.getElementById('page-subtitle');
const refreshDataBtn = document.getElementById('refresh-data');

// Navigation elements
const navVocabulary = document.getElementById('nav-vocabulary');
const navQuiz = document.getElementById('nav-quiz');
const navLessons = document.getElementById('nav-lessons');
const navCategories = document.getElementById('nav-categories');
const navUsers = document.getElementById('nav-users');
const navAchievements = document.getElementById('nav-achievements');
const navAnalytics = document.getElementById('nav-analytics');

// Section elements
const vocabularySection = document.getElementById('vocabulary-section');
const quizSection = document.getElementById('quiz-section');
const lessonsSection = document.getElementById('lessons-section');
const categoriesSection = document.getElementById('categories-section');
const achievementsSection = document.getElementById('achievements-section');
const analyticsSection = document.getElementById('analytics-section');
const usersSection = document.getElementById('users-section');

// Form elements
const lessonForm = document.getElementById('lesson-form');
const categoryForm = document.getElementById('category-form');
const achievementForm = document.getElementById('achievement-form');
const userForm = document.getElementById('user-form');
const editUserForm = document.getElementById('edit-user-form');
const clearLessonForm = document.getElementById('clear-lesson-form');
const clearCategoryForm = document.getElementById('clear-category-form');
const clearAchievementForm = document.getElementById('clear-achievement-form');
const clearUserFormBtn = document.getElementById('clear-user-form');

// List elements
const lessonsList = document.getElementById('lessons-list');
const categoriesGrid = document.getElementById('categories-grid');
const achievementsGrid = document.getElementById('achievements-grid');
const vocabularyForm = document.getElementById('vocabulary-form');
const quizForm = document.getElementById('quiz-form');
const clearVocabularyForm = document.getElementById('clear-vocabulary-form');
const clearQuizForm = document.getElementById('clear-quiz-form');
const vocabularyList = document.getElementById('vocabulary-list');
const quizList = document.getElementById('quiz-list');
const usersList = document.getElementById('users-list');
const filterCategory = document.getElementById('filter-category');
const filterLevel = document.getElementById('filter-level');
const filterQuizCategory = document.getElementById('filter-quiz-category');
const filterQuizLevel = document.getElementById('filter-quiz-level');
const deleteUserBtn = document.getElementById('delete-user-btn');
const toggleUserStatusBtn = document.getElementById('toggle-user-status-btn');
const confirmDeleteUserBtn = document.getElementById('confirm-delete-user-btn');

// State
let currentVocabularyData = [];
let currentQuizData = [];
let currentUsersData = [];
let currentLessonsData = [];
let currentCategoriesData = [];
let currentAchievementsData = [];
let isConnected = false;
let isAuthenticated = false;
let currentUserForDeletion = null;
let currentUserForEdit = null;
let currentUserStatus = 'active'; // Trạng thái người dùng hiện tại

// Current editing items
let currentVocabularyForEdit = null;
let currentVocabularyForDeletion = null;
let currentLessonForEdit = null;
let currentLessonForDeletion = null;
let currentCategoryForEdit = null;
let currentCategoryForDeletion = null;
let currentAchievementForEdit = null;
let currentAchievementForDeletion = null;
let currentQuizForEdit = null;
let currentQuizForDeletion = null;

// Khởi tạo ứng dụng
document.addEventListener('DOMContentLoaded', () => {
    console.log('DOM loaded, initializing app...');
    
    // Kiểm tra các phần tử DOM có tồn tại
    if (!connectionStatus || !userStatus) {
        console.error('Không tìm thấy phần tử DOM cần thiết!');
        return;
    }
    
    // Debug DOM elements
    console.log('Navigation elements:', {
        navVocabulary,
        navQuiz,
        navLessons,
        navCategories,
        navUsers,
        navAchievements,
        navAnalytics
    });
    
    console.log('Section elements:', {
        vocabularySection,
        quizSection,
        lessonsSection,
        categoriesSection,
        usersSection,
        achievementsSection,
        analyticsSection
    });
    
    // Kiểm tra kết nối Firebase
    checkFirebaseConnection();
    
    // Kiểm tra trạng thái đăng nhập
    checkAuthStatus();
    
    // Thiết lập sự kiện chuyển tab
    setupNavigation();
    
    // Thiết lập sự kiện form
    setupForms();
    setupUserForms();
    setupVocabularyForms();
    
    // Thiết lập bộ lọc
    setupFilters();
    
    // Tải dữ liệu ban đầu
    if (isAuthenticated) {
    loadVocabularyData();
    loadQuizData();
    loadUsersData();
        loadLessonsData();
        loadCategoriesData();
        loadAchievementsData();
        loadAnalyticsData();
    }
    
    // Thiết lập sự kiện xóa người dùng
    setupUserDeletion();
});

// Kiểm tra kết nối Firebase
function checkFirebaseConnection() {
    const connectedRef = database.ref('.info/connected');
    
    connectedRef.on('value', (snap) => {
        isConnected = snap.val() === true;
        
        if (isConnected) {
            connectionStatus.textContent = 'Đã kết nối';
            connectionIcon.className = 'fas fa-circle text-success me-1';
        } else {
            connectionStatus.textContent = 'Mất kết nối';
            connectionIcon.className = 'fas fa-circle text-danger me-1';
        }
    });
}

// Kiểm tra trạng thái đăng nhập
function checkAuthStatus() {
    auth.onAuthStateChanged((user) => {
        if (user) {
            isAuthenticated = true;
            if (user.isAnonymous) {
                userStatus.textContent = `Đã đăng nhập ẩn danh`;
            } else {
                userStatus.textContent = `Đã đăng nhập: ${user.email || user.displayName || 'Admin'}`;
            }
            userStatus.className = 'authenticated';
            
            // Khi đã đăng nhập thành công, tải dữ liệu
            loadVocabularyData();
            loadQuizData();
            loadUsersData();
            
        } else {
            auth.signInAnonymously()
                .catch((error) => {
                    console.error('Lỗi đăng nhập ẩn danh:', error);
                });
        }
    });
}

// Thiết lập điều hướng
function setupNavigation() {
    const navItems = [
        { element: navVocabulary, section: 'vocabulary', title: 'Quản lý từ vựng' },
        { element: navQuiz, section: 'quiz', title: 'Quản lý câu hỏi' },
        { element: navLessons, section: 'lessons', title: 'Quản lý bài học' },
        { element: navCategories, section: 'categories', title: 'Quản lý danh mục' },
        { element: navUsers, section: 'users', title: 'Quản lý người dùng' },
        { element: navAchievements, section: 'achievements', title: 'Quản lý thành tích' },
        { element: navAnalytics, section: 'analytics', title: 'Thống kê' }
    ];

    navItems.forEach(item => {
        if (item.element) {
            console.log('Setting up navigation for:', item.section, item.element);
            item.element.addEventListener('click', (e) => {
        e.preventDefault();
                console.log('Navigation clicked:', item.section);
                showSection(item.section, item.title);
            });
        } else {
            console.error('Navigation element not found:', item.section);
        }
    });

    // Refresh data button
    if (refreshDataBtn) {
        refreshDataBtn.addEventListener('click', () => {
        if (isAuthenticated) {
                loadVocabularyData();
                loadQuizData();
            loadUsersData();
                loadLessonsData();
                loadCategoriesData();
                loadAchievementsData();
                loadAnalyticsData();
        }
    });
    }
}

// Hiển thị phần tương ứng
function showSection(section, title) {
    console.log('showSection called:', section, title);
    
    // Cập nhật tiêu đề trang
    if (pageTitle) {
        pageTitle.innerHTML = `<i class="fas fa-${getSectionIcon(section)} me-2"></i>${title || 'Quản lý từ vựng'}`;
    }
    
    // Cập nhật subtitle
    if (pageSubtitle) {
        pageSubtitle.textContent = getSectionSubtitle(section);
    }
    
    // Cập nhật trạng thái active của menu
    const navItems = [navVocabulary, navQuiz, navLessons, navCategories, navUsers, navAchievements, navAnalytics];
    navItems.forEach(nav => {
        if (nav) {
            nav.classList.remove('active');
        }
    });
    
    // Set active cho navigation item tương ứng
    const activeNavMap = {
        'vocabulary': navVocabulary,
        'quiz': navQuiz,
        'lessons': navLessons,
        'categories': navCategories,
        'users': navUsers,
        'achievements': navAchievements,
        'analytics': navAnalytics
    };
    
    if (activeNavMap[section]) {
        activeNavMap[section].classList.add('active');
    }
    
    // Hiển thị phần tương ứng
    const sections = [vocabularySection, quizSection, lessonsSection, categoriesSection, achievementsSection, analyticsSection, usersSection];
    sections.forEach(sec => {
        if (sec) {
            sec.style.display = 'none';
        }
    });
    
    const sectionMap = {
        'vocabulary': vocabularySection,
        'quiz': quizSection,
        'lessons': lessonsSection,
        'categories': categoriesSection,
        'achievements': achievementsSection,
        'analytics': analyticsSection,
        'users': usersSection
    };
    
    if (sectionMap[section]) {
        sectionMap[section].style.display = 'block';
        console.log('Section displayed:', section);
    } else {
        console.error('Section not found:', section);
    }
    
    // Load dữ liệu tương ứng
    if (isAuthenticated) {
        switch (section) {
            case 'vocabulary':
        loadVocabularyData();
                break;
            case 'quiz':
        loadQuizData();
                break;
            case 'users':
        loadUsersData();
                break;
            case 'lessons':
                loadLessonsData();
                break;
            case 'categories':
                loadCategoriesData();
                break;
            case 'achievements':
                loadAchievementsData();
                break;
            case 'analytics':
                loadAnalyticsData();
                break;
        }
    }
}

// Thiết lập sự kiện form
function setupForms() {
    // Form từ vựng
    vocabularyForm.addEventListener('submit', (e) => {
        e.preventDefault();
        
        // Lấy giá trị từ form
        const category = document.getElementById('category').value;
        const level = document.getElementById('level').value;
        const japaneseWord = document.getElementById('japanese-word').value;
        const reading = document.getElementById('reading').value;
        const vietnameseMeaning = document.getElementById('vietnamese-meaning').value;
        const exampleJapanese = document.getElementById('example-japanese').value;
        const exampleVietnamese = document.getElementById('example-vietnamese').value;
        const difficulty = parseFloat(document.getElementById('difficulty').value) || 0.3;
        const imageUrl = document.getElementById('image-url').value;
        
        // Tạo đối tượng từ vựng theo cấu trúc mới
        const vocabularyItem = {
            japanese: japaneseWord,
            reading: reading,
            vietnamese: vietnameseMeaning,
            level: level,
            categories: [category],
            difficulty: difficulty,
            masteryLevel: 'NEW',
            imageUrl: imageUrl || null,
            createdAt: Date.now(),
            updatedAt: Date.now()
        };
        
        // Thêm example sentences nếu có
        if (exampleJapanese && exampleVietnamese) {
            vocabularyItem.exampleSentences = [{
                japanese: exampleJapanese,
                vietnamese: exampleVietnamese
            }];
        }
        
        // Thêm vào Firebase
        addVocabulary(vocabularyItem);
    });
    
    // Xử lý thay đổi loại câu hỏi
    document.getElementById('question-type').addEventListener('change', function() {
        const questionType = this.value;
        
        // Ẩn tất cả các phần tùy chọn
        document.getElementById('multiple-choice-options').style.display = 'none';
        document.getElementById('true-false-options').style.display = 'none';
        document.getElementById('fill-blank-answer').style.display = 'none';
        
        // Hiển thị phần tùy chọn phù hợp với loại câu hỏi
        if (questionType === 'MULTIPLE_CHOICE' || questionType === 'MATCHING') {
            document.getElementById('multiple-choice-options').style.display = 'block';
            document.getElementById('correct-answer').innerHTML = `
                <option value="" selected disabled>Chọn đáp án đúng</option>
                <option value="a">A</option>
                <option value="b">B</option>
                <option value="c">C</option>
                <option value="d">D</option>
            `;
            
            // Đặt lại thuộc tính required cho các trường input
            document.getElementById('option-a').required = true;
            document.getElementById('option-b').required = true;
            document.getElementById('option-c').required = true;
            document.getElementById('option-d').required = true;
            document.getElementById('fill-blank-value').required = false;
        } else if (questionType === 'TRUE_FALSE') {
            document.getElementById('true-false-options').style.display = 'block';
            document.getElementById('correct-answer').innerHTML = `
                <option value="" selected disabled>Chọn đáp án đúng</option>
                <option value="a">A (Đúng)</option>
                <option value="b">B (Sai)</option>
            `;
            
            // Đặt lại thuộc tính required cho các trường input
            document.getElementById('option-a').required = false;
            document.getElementById('option-b').required = false;
            document.getElementById('option-c').required = false;
            document.getElementById('option-d').required = false;
            document.getElementById('fill-blank-value').required = false;
        } else if (questionType === 'FILL_BLANK') {
            document.getElementById('fill-blank-answer').style.display = 'block';
            document.getElementById('correct-answer').innerHTML = `
                <option value="fill_blank" selected>Điền vào chỗ trống</option>
            `;
            
            // Đặt lại thuộc tính required cho các trường input
            document.getElementById('option-a').required = false;
            document.getElementById('option-b').required = false;
            document.getElementById('option-c').required = false;
            document.getElementById('option-d').required = false;
            document.getElementById('fill-blank-value').required = true;
        }
    });
    
    // Form câu hỏi
    quizForm.addEventListener('submit', (e) => {
        e.preventDefault();
        
        // Lấy giá trị từ form
        const category = document.getElementById('quiz-category').value;
        const level = document.getElementById('quiz-level').value;
        const questionType = document.getElementById('question-type').value;
        const question = document.getElementById('question').value;
        const points = parseInt(document.getElementById('points').value, 10) || 10;
        const explanation = document.getElementById('explanation').value;
        
        // Tạo đối tượng câu hỏi dựa trên loại
        let quizItem = {
            question: question,
            type: questionType,
            points: points,
            explanation: explanation || ''
        };
        
        // Xử lý dựa trên loại câu hỏi
        if (questionType === 'MULTIPLE_CHOICE' || questionType === 'MATCHING') {
            const optionA = document.getElementById('option-a').value;
            const optionB = document.getElementById('option-b').value;
            const optionC = document.getElementById('option-c').value;
            const optionD = document.getElementById('option-d').value;
            const correctAnswer = document.getElementById('correct-answer').value;
            
            quizItem.options = {
                a: optionA,
                b: optionB,
                c: optionC,
                d: optionD
            };
            quizItem.correctAnswer = correctAnswer;
        } else if (questionType === 'TRUE_FALSE') {
            const correctAnswer = document.getElementById('correct-answer').value;
            
            quizItem.options = {
                a: 'Đúng',
                b: 'Sai'
            };
            quizItem.correctAnswer = correctAnswer;
        } else if (questionType === 'FILL_BLANK') {
            const fillBlankValue = document.getElementById('fill-blank-value').value;
            quizItem.correctAnswer = fillBlankValue;
        }
        
        // Thêm vào Firebase
        addQuiz(category, level, quizItem);
    });
    
    // Xóa form từ vựng
    clearVocabularyForm.addEventListener('click', () => {
        vocabularyForm.reset();
    });
    
    // Xóa form câu hỏi
    clearQuizForm.addEventListener('click', () => {
        quizForm.reset();
        // Đặt lại hiển thị các phần tùy chọn
        document.getElementById('multiple-choice-options').style.display = 'block';
        document.getElementById('true-false-options').style.display = 'none';
        document.getElementById('fill-blank-answer').style.display = 'none';
        
        // Đặt lại dropdown đáp án đúng
        document.getElementById('correct-answer').innerHTML = `
            <option value="" selected disabled>Chọn đáp án đúng</option>
            <option value="a">A</option>
            <option value="b">B</option>
            <option value="c">C</option>
            <option value="d">D</option>
        `;
        
        // Đặt lại thuộc tính required cho các trường input
        document.getElementById('option-a').required = true;
        document.getElementById('option-b').required = true;
        document.getElementById('option-c').required = true;
        document.getElementById('option-d').required = true;
        document.getElementById('fill-blank-value').required = false;
    });
}

// Thiết lập bộ lọc
function setupFilters() {
    filterCategory.addEventListener('change', () => {
        renderVocabularyList(filterVocabularyData());
    });
    
    filterLevel.addEventListener('change', () => {
        renderVocabularyList(filterVocabularyData());
    });
    
    filterQuizCategory.addEventListener('change', () => {
        renderQuizList(filterQuizData());
    });
    
    filterQuizLevel.addEventListener('change', () => {
        renderQuizList(filterQuizData());
    });
}

// Lọc dữ liệu từ vựng
function filterVocabularyData() {
    const categoryFilter = filterCategory.value;
    const levelFilter = filterLevel.value;
    
    return currentVocabularyData.filter(item => {
        const categoryMatch = categoryFilter === 'all' || 
            (item.categories && item.categories.includes(categoryFilter)) ||
            item.category === categoryFilter;
        const levelMatch = levelFilter === 'all' || item.level === levelFilter;
        
        return categoryMatch && levelMatch;
    });
}

// Lọc dữ liệu câu hỏi
function filterQuizData() {
    const categoryFilter = filterQuizCategory.value;
    const levelFilter = filterQuizLevel.value;
    
    return currentQuizData.filter(item => {
        const categoryMatch = categoryFilter === 'all' || item.category === categoryFilter;
        const levelMatch = levelFilter === 'all' || item.level === levelFilter;
        
        return categoryMatch && levelMatch;
    });
}

// Tải dữ liệu từ vựng
function loadVocabularyData() {
    if (!vocabularyList) return;
    
    vocabularyList.innerHTML = `
        <tr>
            <td colspan="8" class="text-center py-4">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Đang tải...</span>
                </div>
                <div class="mt-2">Đang tải dữ liệu...</div>
            </td>
        </tr>
    `;
    
    database.ref(DB_PATHS.VOCABULARY).once('value')
        .then(snapshot => {
            currentVocabularyData = [];
            
            if (snapshot.exists()) {
                snapshot.forEach(itemSnapshot => {
                            const id = itemSnapshot.key;
                            const data = itemSnapshot.val();
                            
                            currentVocabularyData.push({
                                id,
                                ...data
                    });
                });
            }
            
            renderVocabularyList(currentVocabularyData);
        })
        .catch(error => {
            console.error('Lỗi khi tải dữ liệu từ vựng:', error);
            vocabularyList.innerHTML = `
                <tr>
                    <td colspan="8" class="text-center text-danger py-4">
                        <i class="fas fa-exclamation-triangle me-2"></i>
                        Lỗi khi tải dữ liệu
                    </td>
                </tr>
            `;
        });
}

// Tải dữ liệu câu hỏi
function loadQuizData() {
    quizList.innerHTML = '<tr><td colspan="6" class="text-center">Đang tải dữ liệu...</td></tr>';
    
    console.log("Đang tải dữ liệu câu hỏi từ đường dẫn:", DB_PATHS.QUIZ);
    
    database.ref(DB_PATHS.QUIZ).once('value')
        .then(snapshot => {
            console.log("Đã nhận dữ liệu câu hỏi:", snapshot.val());
            console.log("Snapshot exists:", snapshot.exists());
            
            currentQuizData = [];
            
            if (snapshot.exists()) {
                // Cấu trúc mới: quizzes/{category}/levels/{level}/{quiz_id}/questions/{question_id}
                snapshot.forEach(categorySnapshot => {
                    const category = categorySnapshot.key;
                    console.log("Category:", category);
                    
                    // Bỏ qua các trường không phải levels
                    const levelsSnapshot = categorySnapshot.child('levels');
                    if (levelsSnapshot.exists()) {
                        levelsSnapshot.forEach(levelSnapshot => {
                            const level = levelSnapshot.key;
                            console.log("Level:", level);
                            
                            levelSnapshot.forEach(quizSnapshot => {
                                const quizId = quizSnapshot.key;
                                const quizData = quizSnapshot.val();
                                console.log("Quiz:", quizId, quizData);
                                
                                // Lấy các câu hỏi
                                const questionsSnapshot = quizSnapshot.child('questions');
                                if (questionsSnapshot.exists()) {
                                    questionsSnapshot.forEach(questionSnapshot => {
                                        const questionId = questionSnapshot.key;
                                        const questionData = questionSnapshot.val();
                                        console.log("Question:", questionId, questionData);
                                        
                                        currentQuizData.push({
                                            id: questionId,
                                            quizId: quizId,
                                            category: category,
                                            level: level,
                                            question: questionData.question,
                                            options: questionData.options,
                                            correctAnswer: questionData.correctAnswer,
                                            explanation: questionData.explanation || '',
                                            type: questionData.type || 'MULTIPLE_CHOICE',
                                            points: questionData.points || 10
                                        });
                                    });
                                }
                            });
                        });
                    }
                });
            } else {
                console.log("Không có dữ liệu câu hỏi");
            }
            
            renderQuizList(currentQuizData);
        })
        .catch(error => {
            console.error('Lỗi khi tải dữ liệu câu hỏi:', error);
            quizList.innerHTML = '<tr><td colspan="6" class="text-center text-danger">Lỗi khi tải dữ liệu</td></tr>';
        });
}

// Hiển thị danh sách từ vựng
function renderVocabularyList(data) {
    if (!vocabularyList) return;
    
    if (data.length === 0) {
        vocabularyList.innerHTML = `
            <tr>
                <td colspan="8" class="text-center py-4 text-muted">
                    <i class="fas fa-inbox me-2"></i>
                    Không có dữ liệu
                </td>
            </tr>
        `;
        return;
    }
    
    vocabularyList.innerHTML = data.map(item => {
        const categories = item.categories ? item.categories.join(', ') : item.category || 'N/A';
        const difficulty = item.difficulty ? item.difficulty.toFixed(1) : 'N/A';
        
        return `
            <tr>
                <td class="fw-mono">${item.id}</td>
                <td class="japanese-text fw-bold">${item.japanese}</td>
            <td class="japanese-text">${item.reading}</td>
            <td>${item.vietnamese}</td>
                <td>
                    <span class="badge bg-secondary">${categories}</span>
                </td>
                <td>
                    <span class="badge bg-primary">${item.level}</span>
                </td>
                <td>
                    <span class="badge bg-info">${difficulty}</span>
                </td>
            <td>
                <div class="d-flex justify-content-center gap-1">
                        <button class="btn btn-sm btn-outline-info" onclick="viewVocabularyDetail('${item.id}')" title="Xem chi tiết">
                        <i class="fas fa-eye"></i>
                    </button>
                        <button class="btn btn-sm btn-outline-warning" onclick="editVocabulary('${item.id}')" title="Chỉnh sửa">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteVocabulary('${item.id}')" title="Xóa">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </td>
        </tr>
        `;
    }).join('');
}

// Hiển thị danh sách câu hỏi
function renderQuizList(data) {
    if (data.length === 0) {
        quizList.innerHTML = '<tr><td colspan="6" class="text-center">Không có dữ liệu</td></tr>';
        return;
    }
    
    quizList.innerHTML = data.map(item => {
        const correctAnswer = item.correctAnswer ? item.correctAnswer.toUpperCase() : '';
        
        return `
            <tr>
                <td>${item.id}</td>
                <td>${truncateText(item.question, 100)}</td>
                <td>${correctAnswer}</td>
                <td>${item.category}</td>
                <td>${item.level}</td>
                <td>
                    <div class="d-flex justify-content-center gap-1">
                        <button class="btn btn-sm btn-outline-info btn-action" onclick="viewQuizDetail('${item.id}', '${item.category}', '${item.level}', '${item.quizId}')" title="Xem chi tiết">
                            <i class="fas fa-eye"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-warning btn-action" onclick="editQuiz('${item.id}', '${item.category}', '${item.level}', '${item.quizId}')" title="Chỉnh sửa">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-danger btn-action" onclick="deleteQuiz('${item.id}', '${item.category}', '${item.level}', '${item.quizId}')" title="Xóa">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;
    }).join('');
}

// Thêm từ vựng mới
function addVocabulary(vocabularyItem) {
    // Tạo ID mới dựa trên category và level
    const category = vocabularyItem.categories[0].toLowerCase();
    const level = vocabularyItem.level.toLowerCase();
    
    // Lấy danh sách từ vựng hiện có để tìm số lớn nhất
    database.ref(DB_PATHS.VOCABULARY).once('value')
        .then(snapshot => {
            let maxNumber = 0;
            
            // Tìm số lớn nhất trong các ID hiện có
            if (snapshot.exists()) {
                snapshot.forEach(childSnapshot => {
                    const key = childSnapshot.key;
                    if (key.startsWith(`${category}_${level}_`)) {
                        const numStr = key.replace(`${category}_${level}_`, '');
                        const num = parseInt(numStr, 10);
                        if (!isNaN(num) && num > maxNumber) {
                            maxNumber = num;
                        }
                    }
                });
            }
            
            // Tạo ID mới với số lớn hơn 1
            const newId = `${category}_${level}_${maxNumber + 1}`;
            
            // Thêm dữ liệu với ID được chỉ định
            return database.ref(`${DB_PATHS.VOCABULARY}/${newId}`).set(vocabularyItem);
        })
        .then(() => {
            alert('Thêm từ vựng thành công!');
            vocabularyForm.reset();
            loadVocabularyData();
        })
        .catch(error => {
            console.error('Lỗi khi thêm từ vựng:', error);
            alert('Lỗi khi thêm từ vựng. Vui lòng thử lại!');
        });
}

// Thêm câu hỏi mới
function addQuiz(category, level, quizItem) {
    // Kiểm tra xem đã có quiz cho category và level chưa
    database.ref(`${DB_PATHS.QUIZ}/${category}/levels/${level}`).once('value')
        .then(snapshot => {
            let quizId;
            
            // Nếu chưa có quiz nào, tạo quiz mới
            if (!snapshot.exists() || snapshot.numChildren() === 0) {
                // Tạo quiz mới với ID là quiz_1
                quizId = 'quiz_1';
                
                // Thiết lập thông tin cơ bản cho quiz
                const quizBasicInfo = {
                    id: `${category.toLowerCase()}_${level.toLowerCase()}_1`,
                    title: `${getCategoryName(category)} ${level}`,
                    description: `Học từ vựng về ${getCategoryName(category)} cấp độ ${level}`,
                    timeLimit: 600
                };
                
                // Lưu thông tin cơ bản của quiz
                database.ref(`${DB_PATHS.QUIZ}/${category}/levels/${level}/${quizId}`).update(quizBasicInfo);
                
                // Thiết lập thông tin cơ bản cho category nếu chưa có
                if (!snapshot.parent().parent().exists()) {
                    const categoryInfo = {
                        description: `Từ vựng về ${getCategoryName(category)}`,
                        icon: getCategoryIcon(category)
                    };
                    
                    database.ref(`${DB_PATHS.QUIZ}/${category}`).update(categoryInfo);
                }
            } else {
                // Lấy quiz đầu tiên (thường là quiz_1)
                snapshot.forEach(quizSnapshot => {
                    quizId = quizSnapshot.key;
                    return true; // Chỉ lấy quiz đầu tiên
                });
            }
            
            if (quizId) {
                // Lấy danh sách câu hỏi hiện có để tìm số lớn nhất
                return database.ref(`${DB_PATHS.QUIZ}/${category}/levels/${level}/${quizId}/questions`).once('value')
                    .then(questionsSnapshot => {
                        let maxNumber = 0;
                        
                        // Tìm số lớn nhất trong các ID hiện có
                        if (questionsSnapshot.exists()) {
                            questionsSnapshot.forEach(childSnapshot => {
                                const key = childSnapshot.key;
                                if (key.startsWith('q')) {
                                    const numStr = key.replace('q', '');
                                    const num = parseInt(numStr, 10);
                                    if (!isNaN(num) && num > maxNumber) {
                                        maxNumber = num;
                                    }
                                }
                            });
                        }
                        
                        // Tạo ID mới với số lớn hơn 1
                        const newId = `q${maxNumber + 1}`;
                        
                        // Thêm dữ liệu với ID được chỉ định
                        return database.ref(`${DB_PATHS.QUIZ}/${category}/levels/${level}/${quizId}/questions/${newId}`).set(quizItem);
                    });
            } else {
                throw new Error('Không thể xác định quiz ID');
            }
        })
        .then(() => {
            alert('Thêm câu hỏi thành công!');
            quizForm.reset();
            loadQuizData();
        })
        .catch(error => {
            console.error('Lỗi khi thêm câu hỏi:', error);
            alert('Lỗi khi thêm câu hỏi. Vui lòng thử lại!');
        });
}

// Hàm lấy tên danh mục
function getCategoryName(category) {
    const categoryNames = {
        'VOCABULARY': 'Từ vựng',
        'GRAMMAR': 'Ngữ pháp',
        'KANJI': 'Kanji',
        'LISTENING': 'Nghe',
        'READING': 'Đọc',
        'ANIMALS': 'Động vật',
        'FOOD': 'Đồ ăn',
        'TRANSPORTATION': 'Phương tiện',
        'WEATHER': 'Thời tiết',
        'FAMILY': 'Gia đình',
        'COLORS': 'Màu sắc',
        'NUMBERS': 'Số đếm',
        'TIME': 'Thời gian',
        'VERBS': 'Động từ',
        'ADJECTIVES': 'Tính từ',
        'PLACES': 'Địa điểm',
        'DAILY_LIFE': 'Cuộc sống hàng ngày'
    };
    
    return categoryNames[category] || category;
}

// Hàm lấy biểu tượng cho danh mục
function getCategoryIcon(category) {
    const categoryIcons = {
        'VOCABULARY': '📚',
        'GRAMMAR': '📝',
        'KANJI': '🈁',
        'LISTENING': '👂',
        'READING': '📖',
        'ANIMALS': '🐾',
        'FOOD': '🍱',
        'TRANSPORTATION': '🚆',
        'WEATHER': '🌤',
        'FAMILY': '👨‍👩‍👧‍👦',
        'COLORS': '🎨',
        'NUMBERS': '🔢',
        'TIME': '⏰',
        'VERBS': '🏃',
        'ADJECTIVES': '✨',
        'PLACES': '🏙️',
        'DAILY_LIFE': '🏠'
    };
    
    return categoryIcons[category] || '📚';
}

// Lấy icon cho section
function getSectionIcon(section) {
    const sectionIcons = {
        'vocabulary': 'book',
        'quiz': 'question-circle',
        'lessons': 'chalkboard-teacher',
        'categories': 'tags',
        'users': 'users',
        'achievements': 'trophy',
        'analytics': 'chart-bar'
    };
    
    return sectionIcons[section] || 'book';
}

// Lấy subtitle cho section
function getSectionSubtitle(section) {
    const sectionSubtitles = {
        'vocabulary': 'Quản lý và chỉnh sửa từ vựng tiếng Nhật',
        'quiz': 'Quản lý câu hỏi và bài kiểm tra',
        'lessons': 'Quản lý bài học và nội dung giáo dục',
        'categories': 'Quản lý danh mục và phân loại',
        'users': 'Quản lý người dùng và tài khoản',
        'achievements': 'Quản lý thành tích và phần thưởng',
        'analytics': 'Thống kê và phân tích dữ liệu'
    };
    
    return sectionSubtitles[section] || 'Quản lý hệ thống';
}

// Xem chi tiết từ vựng
function viewVocabularyDetail(id) {
    database.ref(`${DB_PATHS.VOCABULARY}/${id}`).once('value')
        .then(snapshot => {
            if (snapshot.exists()) {
                const data = snapshot.val();
                currentVocabularyForEdit = { id, ...data };
                currentVocabularyForDeletion = id;
                
                const detailContent = document.getElementById('vocabulary-detail-content');
                detailContent.innerHTML = `
                    <div class="row">
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label class="form-label fw-bold">ID:</label>
                                <p class="form-control-plaintext">${id}</p>
                    </div>
                            <div class="mb-3">
                                <label class="form-label fw-bold">Từ tiếng Nhật:</label>
                                <p class="form-control-plaintext japanese-text fs-4 fw-bold">${data.japanese}</p>
                    </div>
                            <div class="mb-3">
                                <label class="form-label fw-bold">Cách đọc:</label>
                                <p class="form-control-plaintext japanese-text fs-5">${data.reading}</p>
                    </div>
                            <div class="mb-3">
                                <label class="form-label fw-bold">Nghĩa tiếng Việt:</label>
                                <p class="form-control-plaintext fs-5">${data.vietnamese}</p>
                    </div>
                    </div>
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label class="form-label fw-bold">Danh mục:</label>
                                <p class="form-control-plaintext">
                                    ${data.categories ? data.categories.map(cat => `<span class="badge bg-secondary me-1">${cat}</span>`).join('') : 'N/A'}
                                </p>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-bold">Cấp độ:</label>
                                <p class="form-control-plaintext">
                                    <span class="badge bg-primary">${data.level}</span>
                                </p>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-bold">Độ khó:</label>
                                <p class="form-control-plaintext">
                                    <span class="badge bg-info">${data.difficulty ? data.difficulty.toFixed(1) : 'N/A'}</span>
                                </p>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-bold">Trạng thái:</label>
                                <p class="form-control-plaintext">
                                    <span class="badge bg-success">${data.masteryLevel || 'NEW'}</span>
                                </p>
                            </div>
                        </div>
                    </div>
                `;
                
                if (data.exampleSentences && data.exampleSentences.length > 0) {
                    const examplesHtml = data.exampleSentences.map(example => `
                        <div class="mb-3 p-3 bg-light rounded">
                            <div class="mb-2">
                                <label class="form-label fw-bold">Ví dụ tiếng Nhật:</label>
                                <p class="form-control-plaintext japanese-text">${example.japanese}</p>
                            </div>
                            <div>
                                <label class="form-label fw-bold">Nghĩa ví dụ:</label>
                                <p class="form-control-plaintext">${example.vietnamese}</p>
                            </div>
                        </div>
                    `).join('');
                    
                    detailContent.innerHTML += `
                        <div class="row mt-3">
                            <div class="col-12">
                                <h6 class="fw-bold">Ví dụ sử dụng:</h6>
                                ${examplesHtml}
                        </div>
                        </div>
                    `;
                }
                
                if (data.imageUrl) {
                    detailContent.innerHTML += `
                        <div class="row mt-3">
                            <div class="col-12">
                                <label class="form-label fw-bold">Hình ảnh:</label>
                                <img src="${data.imageUrl}" alt="Vocabulary image" class="img-fluid rounded" style="max-height: 200px;">
                            </div>
                        </div>
                    `;
                }
                
                // Hiển thị modal
                const modal = new bootstrap.Modal(document.getElementById('vocabulary-detail-modal'));
                modal.show();
            } else {
                alert('Không tìm thấy dữ liệu!');
            }
        })
        .catch(error => {
            console.error('Lỗi khi tải chi tiết từ vựng:', error);
            alert('Lỗi khi tải dữ liệu. Vui lòng thử lại!');
        });
}

// Xem chi tiết câu hỏi
function viewQuizDetail(id, category, level, quizId) {
    database.ref(`${DB_PATHS.QUIZ}/${category}/levels/${level}/${quizId}/questions/${id}`).once('value')
        .then(snapshot => {
            if (snapshot.exists()) {
                const data = snapshot.val();
                
                // Lưu thông tin quiz để chỉnh sửa
                currentQuizForEdit = { id, category, level, quizId, ...data };
                currentQuizForDeletion = { id, category, level, quizId };
                
                const detailContent = document.getElementById('quiz-detail-content');
                detailContent.innerHTML = `
                    <div class="detail-item">
                        <div class="detail-label">ID:</div>
                        <div class="detail-value">${id}</div>
                    </div>
                    <div class="detail-item">
                        <div class="detail-label">Danh mục:</div>
                        <div class="detail-value">${category}</div>
                    </div>
                    <div class="detail-item">
                        <div class="detail-label">Cấp độ:</div>
                        <div class="detail-value">${level}</div>
                    </div>
                    <div class="detail-item">
                        <div class="detail-label">Câu hỏi:</div>
                        <div class="detail-value">${data.question}</div>
                    </div>
                    <div class="detail-item">
                        <div class="detail-label">Loại câu hỏi:</div>
                        <div class="detail-value">${getQuestionTypeName(data.type || 'MULTIPLE_CHOICE')}</div>
                    </div>
                    <div class="detail-item">
                        <div class="detail-label">Điểm:</div>
                        <div class="detail-value">${data.points || 10}</div>
                    </div>
                `;
                
                // Hiển thị thông tin dựa trên loại câu hỏi
                const questionType = data.type || 'MULTIPLE_CHOICE';
                
                if (questionType === 'MULTIPLE_CHOICE' || questionType === 'MATCHING') {
                    if (data.options) {
                        const optionsHtml = Object.entries(data.options).map(([key, value]) => `
                            <div class="detail-item">
                                <div class="detail-label">Lựa chọn ${key.toUpperCase()}:</div>
                                <div class="detail-value">${value}</div>
                            </div>
                        `).join('');
                        
                        detailContent.innerHTML += optionsHtml;
                    }
                    
                    const correctAnswer = data.correctAnswer ? data.correctAnswer.toUpperCase() : '';
                    
                    detailContent.innerHTML += `
                        <div class="detail-item">
                            <div class="detail-label">Đáp án đúng:</div>
                            <div class="detail-value">${correctAnswer}</div>
                        </div>
                    `;
                } else if (questionType === 'TRUE_FALSE') {
                    detailContent.innerHTML += `
                        <div class="detail-item">
                            <div class="detail-label">Lựa chọn A:</div>
                            <div class="detail-value">Đúng</div>
                        </div>
                        <div class="detail-item">
                            <div class="detail-label">Lựa chọn B:</div>
                            <div class="detail-value">Sai</div>
                        </div>
                        <div class="detail-item">
                            <div class="detail-label">Đáp án đúng:</div>
                            <div class="detail-value">${data.correctAnswer === 'a' ? 'A (Đúng)' : 'B (Sai)'}</div>
                        </div>
                    `;
                } else if (questionType === 'FILL_BLANK') {
                    detailContent.innerHTML += `
                        <div class="detail-item">
                            <div class="detail-label">Đáp án đúng:</div>
                            <div class="detail-value">${data.correctAnswer || ''}</div>
                        </div>
                    `;
                }
                
                
                if (data.explanation) {
                    detailContent.innerHTML += `
                        <div class="detail-item">
                            <div class="detail-label">Giải thích:</div>
                            <div class="detail-value">${data.explanation}</div>
                        </div>
                    `;
                }
                
                // Hiển thị modal
                const modal = new bootstrap.Modal(document.getElementById('quiz-detail-modal'));
                modal.show();
            } else {
                alert('Không tìm thấy dữ liệu!');
            }
        })
        .catch(error => {
            console.error('Lỗi khi tải chi tiết câu hỏi:', error);
            alert('Lỗi khi tải dữ liệu. Vui lòng thử lại!');
        });
}

// Chỉnh sửa câu hỏi
function editQuiz(id, category, level, quizId) {
    // Tải dữ liệu quiz từ Firebase
    database.ref(`${DB_PATHS.QUIZ}/${category}/levels/${level}/${quizId}/questions/${id}`).once('value')
        .then(snapshot => {
            if (snapshot.exists()) {
                const data = snapshot.val();
                
                // Lưu thông tin quiz để chỉnh sửa
                currentQuizForEdit = { id, category, level, quizId, ...data };
                
                // Điền thông tin vào form
                document.getElementById('edit-quiz-id').value = currentQuizForEdit.id;
                document.getElementById('edit-quiz-category').value = currentQuizForEdit.category;
                document.getElementById('edit-quiz-level').value = currentQuizForEdit.level;
                document.getElementById('edit-question-type').value = currentQuizForEdit.type || 'MULTIPLE_CHOICE';
                document.getElementById('edit-question').value = currentQuizForEdit.question || '';
                document.getElementById('edit-correct-answer').value = currentQuizForEdit.correctAnswer || '';
                document.getElementById('edit-explanation').value = currentQuizForEdit.explanation || '';

                // Điền options dựa trên loại câu hỏi
                const questionType = currentQuizForEdit.type || 'MULTIPLE_CHOICE';
                
                if (questionType === 'MULTIPLE_CHOICE' && currentQuizForEdit.options) {
                    document.getElementById('edit-option-a').value = currentQuizForEdit.options.a || '';
                    document.getElementById('edit-option-b').value = currentQuizForEdit.options.b || '';
                    document.getElementById('edit-option-c').value = currentQuizForEdit.options.c || '';
                    document.getElementById('edit-option-d').value = currentQuizForEdit.options.d || '';
                } else if (questionType === 'TRUE_FALSE') {
                    const trueFalseValue = currentQuizForEdit.correctAnswer === 'a' ? 'true' : 'false';
                    const trueFalseInput = document.querySelector(`input[name="edit-true-false"][value="${trueFalseValue}"]`);
                    if (trueFalseInput) {
                        trueFalseInput.checked = true;
                    }
                }

                // Hiển thị/ẩn options dựa trên loại câu hỏi
                toggleEditQuizOptions(questionType);

                // Mở modal chỉnh sửa
                const editModal = new bootstrap.Modal(document.getElementById('edit-quiz-modal'));
                editModal.show();
            } else {
                showNotification('Không tìm thấy câu hỏi!', 'error');
            }
        })
        .catch(error => {
            console.error('Lỗi khi tải dữ liệu câu hỏi:', error);
            showNotification('Lỗi khi tải dữ liệu câu hỏi: ' + error.message, 'error');
        });
}

// Xóa câu hỏi
function deleteQuiz(id, category, level, quizId) {
    currentQuizForDeletion = { id, category, level, quizId };
    
    // Hiển thị modal xác nhận
    const confirmModal = new bootstrap.Modal(document.getElementById('confirm-delete-quiz-modal'));
    confirmModal.show();
}

// Xác nhận xóa câu hỏi
function confirmDeleteQuiz() {
    if (!currentQuizForDeletion) return;

    const { id, category, level, quizId } = currentQuizForDeletion;
    
    database.ref(`${DB_PATHS.QUIZ}/${category}/levels/${level}/${quizId}/questions/${id}`).remove()
        .then(() => {
            console.log('Câu hỏi đã được xóa thành công');
            
            // Đóng modal xác nhận
            const confirmModal = bootstrap.Modal.getInstance(document.getElementById('confirm-delete-quiz-modal'));
            if (confirmModal) {
                confirmModal.hide();
            }

            // Đóng modal chi tiết nếu đang mở
            const detailModal = bootstrap.Modal.getInstance(document.getElementById('quiz-detail-modal'));
            if (detailModal) {
                detailModal.hide();
            }

            // Tải lại dữ liệu
            loadQuizData();
            showNotification('Câu hỏi đã được xóa thành công!', 'success');
            
            currentQuizForDeletion = null;
        })
        .catch(error => {
            console.error('Lỗi khi xóa câu hỏi:', error);
            showNotification('Lỗi khi xóa câu hỏi: ' + error.message, 'error');
        });
}

// Lưu thay đổi câu hỏi
function saveQuizChanges() {
    if (!currentQuizForEdit) {
        showNotification('Không tìm thấy câu hỏi để cập nhật!', 'error');
        return;
    }

    const { id, category, level, quizId } = currentQuizForEdit;
    const questionType = document.getElementById('edit-question-type').value;
    
    const updates = {
        [`${DB_PATHS.QUIZ}/${category}/levels/${level}/${quizId}/questions/${id}/question`]: document.getElementById('edit-question').value,
        [`${DB_PATHS.QUIZ}/${category}/levels/${level}/${quizId}/questions/${id}/type`]: questionType,
        [`${DB_PATHS.QUIZ}/${category}/levels/${level}/${quizId}/questions/${id}/correctAnswer`]: document.getElementById('edit-correct-answer').value,
        [`${DB_PATHS.QUIZ}/${category}/levels/${level}/${quizId}/questions/${id}/explanation`]: document.getElementById('edit-explanation').value || null,
        [`${DB_PATHS.QUIZ}/${category}/levels/${level}/${quizId}/questions/${id}/updatedAt`]: Date.now()
    };

    // Cập nhật options dựa trên loại câu hỏi
    if (questionType === 'MULTIPLE_CHOICE') {
        updates[`${DB_PATHS.QUIZ}/${category}/levels/${level}/${quizId}/questions/${id}/options`] = {
            a: document.getElementById('edit-option-a').value,
            b: document.getElementById('edit-option-b').value,
            c: document.getElementById('edit-option-c').value,
            d: document.getElementById('edit-option-d').value
        };
    } else if (questionType === 'TRUE_FALSE') {
        const trueFalseValue = document.querySelector('input[name="edit-true-false"]:checked')?.value;
        updates[`${DB_PATHS.QUIZ}/${category}/levels/${level}/${quizId}/questions/${id}/options`] = {
            a: trueFalseValue === 'true' ? 'Đúng' : 'Sai',
            b: trueFalseValue === 'true' ? 'Sai' : 'Đúng'
        };
        updates[`${DB_PATHS.QUIZ}/${category}/levels/${level}/${quizId}/questions/${id}/correctAnswer`] = trueFalseValue === 'true' ? 'a' : 'b';
    }

    // Cập nhật Firebase
    database.ref().update(updates)
        .then(() => {
            console.log('Thông tin câu hỏi đã được cập nhật');
            
            // Đóng modal chỉnh sửa
            const editModal = bootstrap.Modal.getInstance(document.getElementById('edit-quiz-modal'));
            if (editModal) {
                editModal.hide();
            }

            // Tải lại dữ liệu
            loadQuizData();
            showNotification('Thông tin câu hỏi đã được cập nhật thành công!', 'success');
        })
        .catch(error => {
            console.error('Lỗi khi cập nhật thông tin câu hỏi:', error);
            showNotification('Lỗi khi cập nhật thông tin câu hỏi: ' + error.message, 'error');
        });
}

// Hiển thị/ẩn options dựa trên loại câu hỏi (edit)
function toggleEditQuizOptions(questionType) {
    const multipleChoiceOptions = document.getElementById('edit-multiple-choice-options');
    const trueFalseOptions = document.getElementById('edit-true-false-options');
    
    if (questionType === 'MULTIPLE_CHOICE') {
        multipleChoiceOptions.style.display = 'block';
        trueFalseOptions.style.display = 'none';
    } else if (questionType === 'TRUE_FALSE') {
        multipleChoiceOptions.style.display = 'none';
        trueFalseOptions.style.display = 'block';
    } else {
        multipleChoiceOptions.style.display = 'none';
        trueFalseOptions.style.display = 'none';
    }
}

// Hàm lấy tên loại câu hỏi
function getQuestionTypeName(type) {
    const typeNames = {
        'MULTIPLE_CHOICE': 'Trắc nghiệm',
        'TRUE_FALSE': 'Đúng/Sai',
        'MATCHING': 'Ghép đôi',
        'FILL_BLANK': 'Điền vào chỗ trống'
    };
    
    return typeNames[type] || type;
}

// Xóa từ vựng
function deleteVocabulary(id) {
    currentVocabularyForDeletion = id;
    
    // Hiển thị modal xác nhận
    const confirmModal = new bootstrap.Modal(document.getElementById('confirm-delete-vocabulary-modal'));
    confirmModal.show();
}

// Xác nhận xóa từ vựng
function confirmDeleteVocabulary() {
    if (!currentVocabularyForDeletion) return;

    database.ref(`${DB_PATHS.VOCABULARY}/${currentVocabularyForDeletion}`).remove()
            .then(() => {
            console.log('Từ vựng đã được xóa thành công');
            
            // Đóng modal xác nhận
            const confirmModal = bootstrap.Modal.getInstance(document.getElementById('confirm-delete-vocabulary-modal'));
            if (confirmModal) {
                confirmModal.hide();
            }

            // Đóng modal chi tiết nếu đang mở
            const detailModal = bootstrap.Modal.getInstance(document.getElementById('vocabulary-detail-modal'));
            if (detailModal) {
                detailModal.hide();
            }

            // Tải lại dữ liệu
                loadVocabularyData();
            showNotification('Từ vựng đã được xóa thành công!', 'success');
            
            currentVocabularyForDeletion = null;
            })
            .catch(error => {
                console.error('Lỗi khi xóa từ vựng:', error);
            showNotification('Lỗi khi xóa từ vựng: ' + error.message, 'error');
        });
}

// Chỉnh sửa từ vựng
function editVocabulary(id) {
    const vocabulary = currentVocabularyData.find(v => v.id === id);
    if (vocabulary) {
        currentVocabularyForEdit = vocabulary;
    }
    
    if (!currentVocabularyForEdit) {
        showNotification('Không tìm thấy từ vựng để chỉnh sửa!', 'error');
        return;
    }

    // Điền thông tin vào form
    document.getElementById('edit-vocabulary-id').value = currentVocabularyForEdit.id;
    document.getElementById('edit-japanese').value = currentVocabularyForEdit.japanese || '';
    document.getElementById('edit-reading').value = currentVocabularyForEdit.reading || '';
    document.getElementById('edit-vietnamese').value = currentVocabularyForEdit.vietnamese || '';
    document.getElementById('edit-level').value = currentVocabularyForEdit.level || 'N5';
    document.getElementById('edit-category').value = currentVocabularyForEdit.categories ? currentVocabularyForEdit.categories[0] : currentVocabularyForEdit.category || 'animals';
    document.getElementById('edit-difficulty').value = currentVocabularyForEdit.difficulty || 0.3;
    document.getElementById('edit-mastery-level').value = currentVocabularyForEdit.masteryLevel || 'NEW';
    document.getElementById('edit-image-url').value = currentVocabularyForEdit.imageUrl || '';
    
    if (currentVocabularyForEdit.exampleSentences && currentVocabularyForEdit.exampleSentences.length > 0) {
        document.getElementById('edit-example-japanese').value = currentVocabularyForEdit.exampleSentences[0].japanese || '';
        document.getElementById('edit-example-vietnamese').value = currentVocabularyForEdit.exampleSentences[0].vietnamese || '';
    }

    // Đóng modal chi tiết nếu đang mở
    const detailModal = bootstrap.Modal.getInstance(document.getElementById('vocabulary-detail-modal'));
    if (detailModal) {
        detailModal.hide();
    }

    // Mở modal chỉnh sửa
    const editModal = new bootstrap.Modal(document.getElementById('edit-vocabulary-modal'));
    editModal.show();
}

// Xóa câu hỏi
function deleteQuiz(id, category, level, quizId) {
    if (confirm('Bạn có chắc chắn muốn xóa câu hỏi này?')) {
        database.ref(`${DB_PATHS.QUIZ}/${category}/levels/${level}/${quizId}/questions/${id}`).remove()
            .then(() => {
                alert('Xóa câu hỏi thành công!');
                loadQuizData();
            })
            .catch(error => {
                console.error('Lỗi khi xóa câu hỏi:', error);
                alert('Lỗi khi xóa câu hỏi. Vui lòng thử lại!');
            });
    }
}

// Hàm tiện ích
function truncateText(text, maxLength) {
    if (!text || text.length <= maxLength) {
        return text || '';
    }
    return text.substring(0, maxLength) + '...';
}

// Tải dữ liệu người dùng
function loadUsersData() {
    // Kiểm tra trạng thái đăng nhập
    if (!isAuthenticated) {
        console.error('Chưa đăng nhập');
        return;
    }
    
    // Hiển thị trạng thái đang tải
    usersList.innerHTML = `<tr><td colspan="9" class="text-center">Đang tải dữ liệu người dùng...</td></tr>`;
    
    // Lấy danh sách người dùng từ Firebase
    const usersRef = database.ref(DB_PATHS.USERS);
    usersRef.once('value')
        .then((snapshot) => {
            const usersData = [];
            snapshot.forEach((userSnapshot) => {
                    const userId = userSnapshot.key;
                const userData = userSnapshot.val();
                
                console.log("Raw user data:", userId, userData); // Debug log
                
                // Lấy thông tin từ profile (nếu có)
                const profile = userData.profile || {};
                const progress = userData.progress || {};
                const settings = userData.settings || {};
                const learning = userData.learning || {};
                    
                // Lấy thông tin từ profile (theo cấu trúc thực tế)
                const name = profile.name || 'Không có tên';
                const email = profile.email || 'Không có email';
                const age = profile.age || 0;
                const currentLevel = profile.currentLevel || 'N5';
                const targetLevel = profile.targetLevel || 'N5';
                const avatarUrl = profile.avatarUrl || '';
                const registrationDate = profile.registrationDate || Date.now();
                
                // Lấy thông tin từ progress
                const streak = profile.streak || 0;
                const wordsLearned = progress.wordsLearned || profile.wordsLearned || 0;
                const lessonsCompleted = profile.lessonsCompleted || 0;
                const daysActive = profile.daysActive || 0;
                const lastActiveDate = userData.lastActiveDate || Date.now();
                
                // Lấy thông tin từ settings
                const studyTimeMinutes = profile.studyTimeMinutes || 30;
                const status = userData.status || 'active';
                
                // Lấy thông tin từ learning data
                const learningData = learning || {};
                const vocabularyCount = learningData.vocabulary ? Object.keys(learningData.vocabulary).length : 0;
                const quizResults = learningData.quizResults || {};
                const completedQuizzes = Object.keys(quizResults).filter(key => 
                    typeof quizResults[key] === 'object' && quizResults[key].quizId
                ).length;
                
                // Kiểm tra admin
                const isAdmin = userData.admin || false;
                
                console.log("Processed user:", { id: userId, name, email, isAdmin }); // Debug log
                
                // Thêm người dùng vào danh sách
                usersData.push({
                    id: userId,
                    name,
                    email,
                    age,
                    currentLevel,
                    targetLevel,
                    avatarUrl,
                    registrationDate,
                    streak,
                    wordsLearned,
                    lessonsCompleted,
                    daysActive,
                    lastActiveDate,
                    studyTimeMinutes,
                    status,
                    isAdmin,
                    vocabularyCount,
                    completedQuizzes,
                    learningData,
                    rawData: userData // Lưu dữ liệu gốc để debug
                });
            
            // Hiển thị danh sách người dùng
            currentUsersData = usersData;
            console.log("Total users loaded:", usersData.length); // Log số lượng user đã load
            renderUsersList(usersData);
        })
        .catch((error) => {
            console.error('Lỗi khi tải dữ liệu người dùng:', error);
            usersList.innerHTML = `<tr><td colspan="9" class="text-danger text-center">Lỗi khi tải dữ liệu người dùng: ${error.message}</td></tr>`;
        });
}

// Hiển thị danh sách người dùng
function renderUsersList(data) {
    if (!data || data.length === 0) {
        usersList.innerHTML = `<tr><td colspan="9" class="text-center">Không có dữ liệu người dùng</td></tr>`;
        return;
    }
    
    console.log("Rendering user list with data:", data);
    
    let html = '';
    data.forEach((user) => {
        // Xử lý dữ liệu ngày tháng an toàn
        let registrationDateStr = 'N/A';
        let lastActiveDateStr = 'N/A';
        
        try {
            if (user.registrationDate) {
                registrationDateStr = new Date(user.registrationDate).toLocaleDateString('vi-VN');
            }
            if (user.lastActiveDate) {
                lastActiveDateStr = new Date(user.lastActiveDate).toLocaleDateString('vi-VN');
            }
        } catch (e) {
            console.error("Date conversion error:", e);
        }
        
        const statusClass = user.status === 'active' ? 'text-success' : 'text-danger';
        const statusName = getStatusName(user.status);
        
        // Hiển thị badge admin nếu là admin
        const adminBadge = user.isAdmin ? '<span class="badge bg-danger ms-1">Admin</span>' : '';
        
        html += `
            <tr>
                <td class="user-id">${user.id ? user.id.substring(0, 10) : 'N/A'}...</td>
                <td>${user.email || 'N/A'}</td>
                <td>${user.name || 'N/A'}${adminBadge}</td>
                <td>${registrationDateStr}</td>
                <td>${user.currentLevel || 'N/A'}</td>
                <td class="text-center">
                    <span class="badge bg-info">${user.vocabularyCount || 0}</span>
                </td>
                <td class="text-center">
                    <span class="badge bg-success">${user.completedQuizzes || 0}</span>
                </td>
                <td class="${statusClass}">${statusName}</td>
                <td>
                    <div class="d-flex justify-content-center gap-1">
                        <button class="btn btn-sm btn-outline-info" onclick="viewUserDetail('${user.id}')" title="Xem chi tiết">
                            <i class="fas fa-eye"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-warning" onclick="editUser('${user.id}')" title="Chỉnh sửa">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteUser('${user.id}')" title="Xóa">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;
    });
    
    usersList.innerHTML = html;
    
    // Không cần thêm sự kiện nữa vì đã dùng onclick
}

// Thiết lập form người dùng
function setupUserForms() {
    // Form thêm người dùng
    if (userForm) {
        userForm.addEventListener('submit', (e) => {
            e.preventDefault();
            addUser();
        });
    }

    // Form chỉnh sửa người dùng
    if (editUserForm) {
        editUserForm.addEventListener('submit', (e) => {
            e.preventDefault();
            saveUserChanges();
        });
    }

    // Nút xóa form
    if (clearUserFormBtn) {
        clearUserFormBtn.addEventListener('click', () => {
            clearUserForm();
        });
    }

    // Checkbox thay đổi mật khẩu
    const changePasswordCheckbox = document.getElementById('change-password');
    const passwordFields = document.getElementById('password-fields');
    
    if (changePasswordCheckbox && passwordFields) {
        changePasswordCheckbox.addEventListener('change', () => {
            if (changePasswordCheckbox.checked) {
                passwordFields.style.display = 'block';
                document.getElementById('edit-user-password').required = true;
                document.getElementById('edit-user-confirm-password').required = true;
            } else {
                passwordFields.style.display = 'none';
                document.getElementById('edit-user-password').required = false;
                document.getElementById('edit-user-confirm-password').required = false;
                document.getElementById('edit-user-password').value = '';
                document.getElementById('edit-user-confirm-password').value = '';
            }
        });
    }

    // Nút lưu thay đổi
    const saveUserChangesBtn = document.getElementById('save-user-changes-btn');
    if (saveUserChangesBtn) {
        saveUserChangesBtn.addEventListener('click', () => {
            saveUserChanges();
        });
    }

    // Nút chỉnh sửa người dùng
    const editUserBtn = document.getElementById('edit-user-btn');
    if (editUserBtn) {
        editUserBtn.addEventListener('click', () => {
            editUser();
        });
    }

    // Nút xác nhận xóa người dùng
    if (confirmDeleteUserBtn) {
        confirmDeleteUserBtn.addEventListener('click', () => {
            confirmDeleteUser();
        });
    }

    // Nút toggle trạng thái người dùng
    if (toggleUserStatusBtn) {
        toggleUserStatusBtn.addEventListener('click', () => {
            toggleUserStatus();
        });
    }
}

// Thêm người dùng mới
function addUser() {
    const email = document.getElementById('user-email').value;
    const displayName = document.getElementById('user-display-name').value;
    const password = document.getElementById('user-password').value;
    const level = document.getElementById('user-level').value;
    const status = document.getElementById('user-status').value;
    const avatar = document.getElementById('user-avatar').value;

    const userData = {
        profile: {
            email: email,
            displayName: displayName,
            avatarUrl: avatar || null,
            level: level,
            status: status,
            createdAt: Date.now(),
            updatedAt: Date.now()
        },
        learning: {
            currentLevel: level,
            totalStudyTime: 0,
            streak: 0,
            lastStudyDate: null
        },
        learning_stats: {
            vocabularyLearned: 0,
            lessonsCompleted: 0,
            quizzesTaken: 0,
            averageScore: 0
        }
    };

    // Tạo người dùng trong Firebase Auth (nếu có)
    // Lưu thông tin vào Realtime Database
    const newUserRef = database.ref(DB_PATHS.USERS).push();
    newUserRef.set(userData)
        .then(() => {
            console.log('Người dùng đã được thêm thành công');
            clearUserForm();
            loadUsersData();
            showNotification('Người dùng đã được thêm thành công!', 'success');
        })
        .catch(error => {
            console.error('Lỗi khi thêm người dùng:', error);
            showNotification('Lỗi khi thêm người dùng: ' + error.message, 'error');
        });
}

// Xóa form người dùng
function clearUserForm() {
    if (userForm) {
        userForm.reset();
    }
}

// Chỉnh sửa người dùng
function editUser(userId) {
    if (userId) {
        currentUserForEdit = currentUsersData.find(u => u.id === userId);
    }
    
    if (!currentUserForEdit) return;

    // Điền thông tin vào form
    document.getElementById('edit-user-id').value = currentUserForEdit.id;
    document.getElementById('edit-user-email').value = currentUserForEdit.profile?.email || '';
    document.getElementById('edit-user-display-name').value = currentUserForEdit.profile?.displayName || '';
    document.getElementById('edit-user-level').value = currentUserForEdit.profile?.level || 'N5';
    document.getElementById('edit-user-status').value = currentUserForEdit.profile?.status || 'active';
    document.getElementById('edit-user-avatar').value = currentUserForEdit.profile?.avatarUrl || '';

    // Đóng modal chi tiết và mở modal chỉnh sửa
    const detailModal = bootstrap.Modal.getInstance(document.getElementById('user-detail-modal'));
    if (detailModal) {
        detailModal.hide();
    }

    const editModal = new bootstrap.Modal(document.getElementById('edit-user-modal'));
    editModal.show();
}

// Lưu thay đổi người dùng
function saveUserChanges() {
    const userId = document.getElementById('edit-user-id').value;
    const email = document.getElementById('edit-user-email').value;
    const displayName = document.getElementById('edit-user-display-name').value;
    const level = document.getElementById('edit-user-level').value;
    const status = document.getElementById('edit-user-status').value;
    const avatar = document.getElementById('edit-user-avatar').value;
    const changePassword = document.getElementById('change-password').checked;
    const newPassword = document.getElementById('edit-user-password').value;
    const confirmPassword = document.getElementById('edit-user-confirm-password').value;

    // Kiểm tra mật khẩu nếu có thay đổi
    if (changePassword) {
        if (newPassword !== confirmPassword) {
            showNotification('Mật khẩu xác nhận không khớp!', 'error');
            return;
        }
        if (newPassword.length < 6) {
            showNotification('Mật khẩu phải có ít nhất 6 ký tự!', 'error');
            return;
        }
    }

    const updates = {
        [`${DB_PATHS.USERS}/${userId}/profile/email`]: email,
        [`${DB_PATHS.USERS}/${userId}/profile/displayName`]: displayName,
        [`${DB_PATHS.USERS}/${userId}/profile/level`]: level,
        [`${DB_PATHS.USERS}/${userId}/profile/status`]: status,
        [`${DB_PATHS.USERS}/${userId}/profile/avatarUrl`]: avatar || null,
        [`${DB_PATHS.USERS}/${userId}/profile/updatedAt`]: Date.now(),
        [`${DB_PATHS.USERS}/${userId}/learning/currentLevel`]: level
    };

    // Cập nhật Firebase
    database.ref().update(updates)
        .then(() => {
            console.log('Thông tin người dùng đã được cập nhật');
            
            // Đóng modal chỉnh sửa
            const editModal = bootstrap.Modal.getInstance(document.getElementById('edit-user-modal'));
            if (editModal) {
                editModal.hide();
            }

            // Tải lại dữ liệu
            loadUsersData();
            showNotification('Thông tin người dùng đã được cập nhật thành công!', 'success');
        })
        .catch(error => {
            console.error('Lỗi khi cập nhật thông tin người dùng:', error);
            showNotification('Lỗi khi cập nhật thông tin người dùng: ' + error.message, 'error');
        });
}

// Xóa người dùng
function deleteUser(userId) {
    if (!userId) return;

    currentUserForDeletion = userId;
    
    // Hiển thị modal xác nhận
    const confirmModal = new bootstrap.Modal(document.getElementById('confirm-delete-user-modal'));
    confirmModal.show();
}

// Xác nhận xóa người dùng
function confirmDeleteUser() {
    if (!currentUserForDeletion) return;

    database.ref(`${DB_PATHS.USERS}/${currentUserForDeletion}`).remove()
        .then(() => {
            console.log('Người dùng đã được xóa thành công');
            
            // Đóng modal xác nhận
            const confirmModal = bootstrap.Modal.getInstance(document.getElementById('confirm-delete-user-modal'));
            if (confirmModal) {
                confirmModal.hide();
            }

            // Đóng modal chi tiết nếu đang mở
            const detailModal = bootstrap.Modal.getInstance(document.getElementById('user-detail-modal'));
            if (detailModal) {
                detailModal.hide();
            }

            // Tải lại dữ liệu
            loadUsersData();
            showNotification('Người dùng đã được xóa thành công!', 'success');
            
            currentUserForDeletion = null;
        })
        .catch(error => {
            console.error('Lỗi khi xóa người dùng:', error);
            showNotification('Lỗi khi xóa người dùng: ' + error.message, 'error');
        });
}

// Toggle trạng thái người dùng
function toggleUserStatus() {
    if (!currentUserForEdit) return;

    const currentStatus = currentUserForEdit.profile?.status || 'active';
    const newStatus = currentStatus === 'active' ? 'inactive' : 'active';
    
    const updates = {
        [`${DB_PATHS.USERS}/${currentUserForEdit.id}/profile/status`]: newStatus,
        [`${DB_PATHS.USERS}/${currentUserForEdit.id}/profile/updatedAt`]: Date.now()
    };

    database.ref().update(updates)
        .then(() => {
            console.log('Trạng thái người dùng đã được cập nhật');
            
            // Đóng modal chi tiết
            const detailModal = bootstrap.Modal.getInstance(document.getElementById('user-detail-modal'));
            if (detailModal) {
                detailModal.hide();
            }

            // Tải lại dữ liệu
            loadUsersData();
            showNotification(`Trạng thái người dùng đã được chuyển thành ${newStatus === 'active' ? 'hoạt động' : 'không hoạt động'}!`, 'success');
        })
        .catch(error => {
            console.error('Lỗi khi cập nhật trạng thái người dùng:', error);
            showNotification('Lỗi khi cập nhật trạng thái người dùng: ' + error.message, 'error');
        });
}

// Hiển thị thông báo
function showNotification(message, type = 'info') {
    // Tạo toast notification
    const toastContainer = document.getElementById('toast-container') || createToastContainer();
    
    const toastId = 'toast-' + Date.now();
    const toastHtml = `
        <div class="toast" id="${toastId}" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="toast-header bg-${type === 'success' ? 'success' : type === 'error' ? 'danger' : 'info'} text-white">
                <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-triangle' : 'info-circle'} me-2"></i>
                <strong class="me-auto">Thông báo</strong>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
            <div class="toast-body">
                ${message}
            </div>
        </div>
    `;
    
    toastContainer.insertAdjacentHTML('beforeend', toastHtml);
    
    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement);
    toast.show();
    
    // Tự động xóa toast sau khi ẩn
    toastElement.addEventListener('hidden.bs.toast', () => {
        toastElement.remove();
    });
}

// Tạo container cho toast
function createToastContainer() {
    const container = document.createElement('div');
    container.id = 'toast-container';
    container.className = 'toast-container position-fixed top-0 end-0 p-3';
    container.style.zIndex = '9999';
    document.body.appendChild(container);
    return container;
}

// Thiết lập form từ vựng
function setupVocabularyForms() {
    // Nút chỉnh sửa từ vựng
    const editVocabularyBtn = document.getElementById('edit-vocabulary-btn');
    if (editVocabularyBtn) {
        editVocabularyBtn.addEventListener('click', () => {
            if (currentVocabularyForEdit) {
                editVocabulary(currentVocabularyForEdit.id);
            }
        });
    }

    // Nút xóa từ vựng
    const deleteVocabularyBtn = document.getElementById('delete-vocabulary-btn');
    if (deleteVocabularyBtn) {
        deleteVocabularyBtn.addEventListener('click', () => {
            if (currentVocabularyForDeletion) {
                deleteVocabulary(currentVocabularyForDeletion);
            }
        });
    }

    // Nút lưu thay đổi từ vựng
    const saveVocabularyChangesBtn = document.getElementById('save-vocabulary-changes-btn');
    if (saveVocabularyChangesBtn) {
        saveVocabularyChangesBtn.addEventListener('click', () => {
            saveVocabularyChanges();
        });
    }

    // Nút xác nhận xóa từ vựng
    const confirmDeleteVocabularyBtn = document.getElementById('confirm-delete-vocabulary-btn');
    if (confirmDeleteVocabularyBtn) {
        confirmDeleteVocabularyBtn.addEventListener('click', () => {
            confirmDeleteVocabulary();
        });
    }

    // Nút chỉnh sửa câu hỏi
    const editQuizBtn = document.getElementById('edit-quiz-btn');
    if (editQuizBtn) {
        editQuizBtn.addEventListener('click', () => {
            if (currentQuizForEdit) {
                editQuiz(currentQuizForEdit.id, currentQuizForEdit.category, currentQuizForEdit.level, currentQuizForEdit.quizId);
            }
        });
    }

    // Nút xóa câu hỏi
    const deleteQuizBtn = document.getElementById('delete-quiz-btn');
    if (deleteQuizBtn) {
        deleteQuizBtn.addEventListener('click', () => {
            if (currentQuizForDeletion) {
                deleteQuiz(currentQuizForDeletion.id, currentQuizForDeletion.category, currentQuizForDeletion.level, currentQuizForDeletion.quizId);
            }
        });
    }

    // Nút lưu thay đổi câu hỏi
    const saveQuizChangesBtn = document.getElementById('save-quiz-changes-btn');
    if (saveQuizChangesBtn) {
        saveQuizChangesBtn.addEventListener('click', () => {
            saveQuizChanges();
        });
    }

    // Nút xác nhận xóa câu hỏi
    const confirmDeleteQuizBtn = document.getElementById('confirm-delete-quiz-btn');
    if (confirmDeleteQuizBtn) {
        confirmDeleteQuizBtn.addEventListener('click', () => {
            confirmDeleteQuiz();
        });
    }

    // Event listener cho thay đổi loại câu hỏi (edit)
    const editQuestionType = document.getElementById('edit-question-type');
    if (editQuestionType) {
        editQuestionType.addEventListener('change', (e) => {
            toggleEditQuizOptions(e.target.value);
        });
    }
}

// Lưu thay đổi từ vựng
function saveVocabularyChanges() {
    const vocabularyId = document.getElementById('edit-vocabulary-id').value;
    const japanese = document.getElementById('edit-japanese').value;
    const reading = document.getElementById('edit-reading').value;
    const vietnamese = document.getElementById('edit-vietnamese').value;
    const level = document.getElementById('edit-level').value;
    const category = document.getElementById('edit-category').value;
    const difficulty = parseFloat(document.getElementById('edit-difficulty').value);
    const masteryLevel = document.getElementById('edit-mastery-level').value;
    const imageUrl = document.getElementById('edit-image-url').value;
    const exampleJapanese = document.getElementById('edit-example-japanese').value;
    const exampleVietnamese = document.getElementById('edit-example-vietnamese').value;

    const updates = {
        [`${DB_PATHS.VOCABULARY}/${vocabularyId}/japanese`]: japanese,
        [`${DB_PATHS.VOCABULARY}/${vocabularyId}/reading`]: reading,
        [`${DB_PATHS.VOCABULARY}/${vocabularyId}/vietnamese`]: vietnamese,
        [`${DB_PATHS.VOCABULARY}/${vocabularyId}/level`]: level,
        [`${DB_PATHS.VOCABULARY}/${vocabularyId}/categories`]: [category],
        [`${DB_PATHS.VOCABULARY}/${vocabularyId}/difficulty`]: difficulty,
        [`${DB_PATHS.VOCABULARY}/${vocabularyId}/masteryLevel`]: masteryLevel,
        [`${DB_PATHS.VOCABULARY}/${vocabularyId}/imageUrl`]: imageUrl || null,
        [`${DB_PATHS.VOCABULARY}/${vocabularyId}/updatedAt`]: Date.now()
    };

    if (exampleJapanese && exampleVietnamese) {
        updates[`${DB_PATHS.VOCABULARY}/${vocabularyId}/exampleSentences`] = [{
            japanese: exampleJapanese,
            vietnamese: exampleVietnamese
        }];
    }

    // Cập nhật Firebase
    database.ref().update(updates)
        .then(() => {
            console.log('Thông tin từ vựng đã được cập nhật');
            
            // Đóng modal chỉnh sửa
            const editModal = bootstrap.Modal.getInstance(document.getElementById('edit-vocabulary-modal'));
            if (editModal) {
                editModal.hide();
            }

            // Tải lại dữ liệu
            loadVocabularyData();
            showNotification('Thông tin từ vựng đã được cập nhật thành công!', 'success');
        })
        .catch(error => {
            console.error('Lỗi khi cập nhật thông tin từ vựng:', error);
            showNotification('Lỗi khi cập nhật thông tin từ vựng: ' + error.message, 'error');
        });
}

// Tải dữ liệu bài học
function loadLessonsData() {
    if (!lessonsList) return;
    
    lessonsList.innerHTML = `
        <tr>
            <td colspan="6" class="text-center py-4">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Đang tải...</span>
                </div>
                <div class="mt-2">Đang tải dữ liệu...</div>
            </td>
        </tr>
    `;
    
    database.ref(DB_PATHS.LESSONS).once('value')
        .then(snapshot => {
            currentLessonsData = [];
            
            if (snapshot.exists()) {
                snapshot.forEach(lessonSnapshot => {
                    const id = lessonSnapshot.key;
                    const data = lessonSnapshot.val();
                    
                    currentLessonsData.push({
                        id,
                        ...data
                    });
                });
            }
            
            renderLessonsList(currentLessonsData);
        })
        .catch(error => {
            console.error('Lỗi khi tải dữ liệu bài học:', error);
            lessonsList.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center text-danger py-4">
                        <i class="fas fa-exclamation-triangle me-2"></i>
                        Lỗi khi tải dữ liệu
                    </td>
                </tr>
            `;
        });
}

// Hiển thị danh sách bài học
function renderLessonsList(data) {
    if (!lessonsList) return;
    
    if (data.length === 0) {
        lessonsList.innerHTML = `
            <tr>
                <td colspan="6" class="text-center py-4 text-muted">
                    <i class="fas fa-inbox me-2"></i>
                    Không có dữ liệu
                </td>
            </tr>
        `;
        return;
    }
    
    lessonsList.innerHTML = data.map(lesson => `
        <tr>
            <td class="fw-mono">${lesson.id}</td>
            <td class="fw-bold">${lesson.title}</td>
            <td>
                <span class="badge bg-secondary">${lesson.category}</span>
            </td>
            <td>
                <span class="badge bg-primary">${lesson.level}</span>
            </td>
            <td>
                <span class="badge bg-info">${lesson.estimatedTimeMinutes || 15} phút</span>
            </td>
            <td>
                <div class="d-flex justify-content-center gap-1">
                    <button class="btn btn-sm btn-outline-info" onclick="viewLessonDetail('${lesson.id}')" title="Xem chi tiết">
                        <i class="fas fa-eye"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-warning" onclick="editLesson('${lesson.id}')" title="Chỉnh sửa">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteLesson('${lesson.id}')" title="Xóa">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

// Tải dữ liệu danh mục
function loadCategoriesData() {
    if (!categoriesGrid) return;
    
    categoriesGrid.innerHTML = `
        <div class="col-12 text-center py-4">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Đang tải...</span>
            </div>
            <div class="mt-2">Đang tải dữ liệu...</div>
        </div>
    `;
    
    database.ref(DB_PATHS.CATEGORIES).once('value')
        .then(snapshot => {
            currentCategoriesData = [];
            
            if (snapshot.exists()) {
                snapshot.forEach(categorySnapshot => {
                    const id = categorySnapshot.key;
                    const data = categorySnapshot.val();
                    
                    currentCategoriesData.push({
                        id,
                        ...data
                    });
                });
            }
            
            renderCategoriesGrid(currentCategoriesData);
        })
        .catch(error => {
            console.error('Lỗi khi tải dữ liệu danh mục:', error);
            categoriesGrid.innerHTML = `
                <div class="col-12 text-center text-danger py-4">
                    <i class="fas fa-exclamation-triangle me-2"></i>
                    Lỗi khi tải dữ liệu
                </div>
            `;
        });
}

// Hiển thị grid danh mục
function renderCategoriesGrid(data) {
    if (!categoriesGrid) return;
    
    if (data.length === 0) {
        categoriesGrid.innerHTML = `
            <div class="col-12 text-center py-4 text-muted">
                <i class="fas fa-inbox me-2"></i>
                Không có dữ liệu
            </div>
        `;
        return;
    }
    
    categoriesGrid.innerHTML = data.map(category => `
        <div class="col-md-6 col-lg-4 mb-4">
            <div class="category-card">
                <div class="display-4 mb-3">${category.iconUrl || '📚'}</div>
                <h5 class="card-title">${category.name}</h5>
                <p class="card-text">${category.description}</p>
                <div class="d-flex justify-content-center gap-2 mb-3">
                    <span class="badge bg-primary">${category.vocabularyCount || 0} từ</span>
                    <span class="badge bg-secondary">${category.lessonCount || 0} bài</span>
                </div>
                <div class="d-flex justify-content-center gap-1">
                    <button class="btn btn-sm btn-outline-info" onclick="viewCategoryDetail('${category.id}')" title="Xem chi tiết">
                        <i class="fas fa-eye"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-warning" onclick="editCategory('${category.id}')" title="Chỉnh sửa">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteCategory('${category.id}')" title="Xóa">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </div>
        </div>
    `).join('');
}

// Tải dữ liệu thành tích
function loadAchievementsData() {
    if (!achievementsGrid) return;
    
    achievementsGrid.innerHTML = `
        <div class="col-12 text-center py-4">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Đang tải...</span>
            </div>
            <div class="mt-2">Đang tải dữ liệu...</div>
        </div>
    `;
    
    database.ref(DB_PATHS.ACHIEVEMENTS).once('value')
        .then(snapshot => {
            currentAchievementsData = [];
            
            if (snapshot.exists()) {
                snapshot.forEach(achievementSnapshot => {
                    const id = achievementSnapshot.key;
                    const data = achievementSnapshot.val();
                    
                    currentAchievementsData.push({
                        id,
                        ...data
                    });
                });
            }
            
            renderAchievementsGrid(currentAchievementsData);
        })
        .catch(error => {
            console.error('Lỗi khi tải dữ liệu thành tích:', error);
            achievementsGrid.innerHTML = `
                <div class="col-12 text-center text-danger py-4">
                    <i class="fas fa-exclamation-triangle me-2"></i>
                    Lỗi khi tải dữ liệu
                </div>
            `;
        });
}

// Hiển thị grid thành tích
function renderAchievementsGrid(data) {
    if (!achievementsGrid) return;
    
    if (data.length === 0) {
        achievementsGrid.innerHTML = `
            <div class="col-12 text-center py-4 text-muted">
                <i class="fas fa-inbox me-2"></i>
                Không có dữ liệu
            </div>
        `;
        return;
    }
    
    achievementsGrid.innerHTML = data.map(achievement => {
        const rarityColors = {
            'common': '#6B7280',
            'uncommon': '#10B981',
            'rare': '#3B82F6',
            'epic': '#8B5CF6',
            'legendary': '#F59E0B'
        };
        
        const rarityNames = {
            'common': 'Thường',
            'uncommon': 'Hiếm',
            'rare': 'Rất hiếm',
            'epic': 'Huyền thoại',
            'legendary': 'Thần thoại'
        };
        
        return `
            <div class="col-md-6 col-lg-4 mb-4">
                <div class="achievement-card" style="border-left: 4px solid ${rarityColors[achievement.rarity] || '#6B7280'};">
                    <div class="display-4 mb-3">${achievement.iconUrl || '🏆'}</div>
                    <h5 class="card-title">${achievement.title}</h5>
                    <p class="card-text">${achievement.description}</p>
                    <div class="d-flex justify-content-center gap-2 mb-3">
                        <span class="badge" style="background-color: ${rarityColors[achievement.rarity] || '#6B7280'}">${rarityNames[achievement.rarity] || 'Thường'}</span>
                        <span class="badge bg-warning">${achievement.points} điểm</span>
                    </div>
                    <div class="d-flex justify-content-center gap-1">
                        <button class="btn btn-sm btn-outline-info" onclick="viewAchievementDetail('${achievement.id}')" title="Xem chi tiết">
                            <i class="fas fa-eye"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-warning" onclick="editAchievement('${achievement.id}')" title="Chỉnh sửa">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteAchievement('${achievement.id}')" title="Xóa">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

// Xem chi tiết người dùng
function viewUserDetail(userId) {
    // Tìm dữ liệu người dùng
    const user = currentUsersData.find((u) => u.id === userId);
    
    if (!user) {
        showNotification('Không tìm thấy thông tin người dùng!', 'error');
        return;
    }
    
    currentUserForEdit = user;
    
    // Lưu trữ thông tin người dùng đang xem
    currentUserForDeletion = userId;
    currentUserStatus = user.profile?.status || 'active';
                
    // Cập nhật nút chuyển đổi trạng thái
    toggleUserStatusBtn.textContent = currentUserStatus === 'active' ? 'Vô hiệu hóa' : 'Kích hoạt';
    toggleUserStatusBtn.classList.toggle('btn-warning', currentUserStatus === 'active');
    toggleUserStatusBtn.classList.toggle('btn-success', currentUserStatus !== 'active');
    
    // Định dạng thời gian
    const lastActiveDate = new Date(user.lastActiveDate).toLocaleDateString('vi-VN');
    const registrationDate = new Date(user.registrationDate).toLocaleDateString('vi-VN');
                
    // Tạo HTML chi tiết người dùng
    const detailContent = `
        <div class="user-detail">
            <div class="user-avatar text-center mb-3">
                <img src="${user.avatarUrl || 'https://via.placeholder.com/100'}" alt="Avatar" class="rounded-circle" width="100">
            </div>
            
            <h4 class="text-center mb-3">${user.name}</h4>
            <p class="text-muted text-center">${user.email}</p>
            
            <div class="row mt-4">
                <div class="col-md-6">
                    <div class="mb-3">
                        <strong>ID:</strong>
                        <span class="text-muted">${user.id}</span>
                    </div>
                    <div class="mb-3">
                        <strong>Tuổi:</strong>
                        <span class="text-muted">${user.age}</span>
                    </div>
                    <div class="mb-3">
                        <strong>Ngày đăng ký:</strong>
                        <span class="text-muted">${registrationDate}</span>
                    </div>
                    <div class="mb-3">
                        <strong>Trạng thái:</strong>
                        <span class="text-muted">${getStatusName(user.status)}</span>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="mb-3">
                        <strong>Trình độ hiện tại:</strong>
                        <span class="text-muted">${user.currentLevel}</span>
                    </div>
                    <div class="mb-3">
                        <strong>Trình độ mục tiêu:</strong>
                        <span class="text-muted">${user.targetLevel}</span>
                    </div>
                    <div class="mb-3">
                        <strong>Hoạt động cuối:</strong>
                        <span class="text-muted">${lastActiveDate}</span>
                    </div>
                    <div class="mb-3">
                        <strong>Thời gian học (phút/ngày):</strong>
                        <span class="text-muted">${user.studyTimeMinutes}</span>
                    </div>
                </div>
                    </div>
                    
            <div class="row mt-3">
                <div class="col-md-12">
                    <div class="progress-stats">
                        <h5 class="mb-3">Thống kê học tập</h5>
                        
                        <div class="mb-2">
                            <div class="d-flex justify-content-between">
                                <span>Streak ngày học liên tiếp:</span>
                                <span class="text-primary">${user.streak}</span>
                    </div>
                    </div>
                        
                        <div class="mb-2">
                            <div class="d-flex justify-content-between">
                                <span>Số ngày học tích cực:</span>
                                <span class="text-primary">${user.daysActive}</span>
                    </div>
                    </div>
                        
                        <div class="mb-2">
                            <div class="d-flex justify-content-between">
                                <span>Số từ vựng đã học:</span>
                                <span class="text-primary">${user.wordsLearned}</span>
                            </div>
                    </div>
                    
                        <div class="mb-2">
                            <div class="d-flex justify-content-between">
                                <span>Số bài học đã hoàn thành:</span>
                                <span class="text-primary">${user.lessonsCompleted}</span>
                        </div>
                        </div>
                        </div>
                </div>
                        </div>
                    </div>
                `;
                
                // Hiển thị modal
    document.getElementById('user-detail-content').innerHTML = detailContent;
    const userDetailModal = new bootstrap.Modal(document.getElementById('user-detail-modal'));
    userDetailModal.show();
}

// Thiết lập sự kiện xóa người dùng
function setupUserDeletion() {
    // Nút xóa trong modal chi tiết người dùng
    deleteUserBtn.addEventListener('click', () => {
        // Hiển thị modal xác nhận
        const confirmModal = new bootstrap.Modal(document.getElementById('confirm-delete-user-modal'));
        confirmModal.show();
        
        // Đóng modal chi tiết
        bootstrap.Modal.getInstance(document.getElementById('user-detail-modal')).hide();
    });
    
    // Nút vô hiệu hóa/kích hoạt tài khoản
    toggleUserStatusBtn.addEventListener('click', () => {
        if (currentUserForDeletion) {
            const newStatus = currentUserStatus === 'active' ? 'inactive' : 'active';
            toggleUserStatus(currentUserForDeletion, newStatus);
        }
    });
    
    // Nút xác nhận xóa
    confirmDeleteUserBtn.addEventListener('click', () => {
        if (currentUserForDeletion) {
            deleteUser(currentUserForDeletion);
        }
    });
}

// Vô hiệu hóa/kích hoạt tài khoản người dùng
function toggleUserStatus(userId, newStatus) {
    database.ref(`${DB_PATHS.USERS}/${userId}/status`).set(newStatus)
        .then(() => {
            // Đóng modal chi tiết
            bootstrap.Modal.getInstance(document.getElementById('user-detail-modal')).hide();
            
            // Tải lại dữ liệu người dùng
            loadUsersData();
            
            // Thông báo thành công
            const action = newStatus === 'active' ? 'Kích hoạt' : 'Vô hiệu hóa';
            alert(`${action} tài khoản thành công!`);
        })
        .catch(error => {
            console.error('Lỗi khi cập nhật trạng thái người dùng:', error);
            alert('Lỗi khi cập nhật trạng thái người dùng. Vui lòng thử lại!');
        });
}

// Chuẩn bị xóa người dùng (từ nút xóa trực tiếp trong bảng)
function prepareDeleteUser(userId) {
    currentUserForDeletion = userId;
    const confirmModal = new bootstrap.Modal(document.getElementById('confirm-delete-user-modal'));
    confirmModal.show();
}

// Xóa người dùng
function deleteUser(userId) {
    database.ref(`${DB_PATHS.USERS}/${userId}`).remove()
        .then(() => {
            // Đóng modal xác nhận
            bootstrap.Modal.getInstance(document.getElementById('confirm-delete-user-modal')).hide();
            
            // Tải lại dữ liệu người dùng
            loadUsersData();
            
            // Thông báo thành công
            alert('Xóa người dùng thành công!');
        })
        .catch(error => {
            console.error('Lỗi khi xóa người dùng:', error);
            alert('Lỗi khi xóa người dùng. Vui lòng thử lại!');
        });
}

// Chuyển đổi trạng thái thành tên hiển thị
function getStatusName(status) {
    switch (status) {
        case 'active':
            return 'Hoạt động';
        case 'inactive':
            return 'Không hoạt động';
        case 'banned':
            return 'Bị khóa';
        default:
            return 'Không xác định';
    }
}

// Tải dữ liệu thống kê
function loadAnalyticsData() {
    // Tải thống kê từ vựng
    database.ref(DB_PATHS.VOCABULARY).once('value')
        .then(snapshot => {
            const totalVocabulary = snapshot.numChildren();
            const totalVocabularyElement = document.getElementById('total-vocabulary');
            if (totalVocabularyElement) {
                totalVocabularyElement.textContent = totalVocabulary;
            }
        })
        .catch(error => {
            console.error('Lỗi khi tải thống kê từ vựng:', error);
        });

    // Tải thống kê người dùng
    database.ref(DB_PATHS.USERS).once('value')
        .then(snapshot => {
            const totalUsers = snapshot.numChildren();
            const totalUsersElement = document.getElementById('total-users');
            if (totalUsersElement) {
                totalUsersElement.textContent = totalUsers;
            }
        })
        .catch(error => {
            console.error('Lỗi khi tải thống kê người dùng:', error);
        });

    // Tải thống kê câu hỏi
    database.ref(DB_PATHS.QUIZ).once('value')
        .then(snapshot => {
            let totalQuizzes = 0;
            if (snapshot.exists()) {
                snapshot.forEach(categorySnapshot => {
                    const levelsSnapshot = categorySnapshot.child('levels');
                    if (levelsSnapshot.exists()) {
                        levelsSnapshot.forEach(levelSnapshot => {
                            levelSnapshot.forEach(quizSnapshot => {
                                const questionsSnapshot = quizSnapshot.child('questions');
                                if (questionsSnapshot.exists()) {
                                    totalQuizzes += questionsSnapshot.numChildren();
                                }
                            });
                        });
                    }
                });
            }
            const totalQuizzesElement = document.getElementById('total-quizzes');
            if (totalQuizzesElement) {
                totalQuizzesElement.textContent = totalQuizzes;
            }
        })
        .catch(error => {
            console.error('Lỗi khi tải thống kê câu hỏi:', error);
        });

    // Tải thống kê bài học
    database.ref(DB_PATHS.LESSONS).once('value')
        .then(snapshot => {
            const totalLessons = snapshot.numChildren();
            const totalLessonsElement = document.getElementById('total-lessons');
            if (totalLessonsElement) {
                totalLessonsElement.textContent = totalLessons;
            }
        })
        .catch(error => {
            console.error('Lỗi khi tải thống kê bài học:', error);
        });
}

// Xem chi tiết bài học
function viewLessonDetail(lessonId) {
    const lesson = currentLessonsData.find(l => l.id === lessonId);
    
    if (!lesson) {
        showNotification('Không tìm thấy bài học!', 'error');
        return;
    }
    
    currentLessonForEdit = lesson;
    currentLessonForDeletion = lessonId;
    
    showNotification(`Bài học: ${lesson.title}`, 'info');
}

// Chỉnh sửa bài học
function editLesson(lessonId) {
    const lesson = currentLessonsData.find(l => l.id === lessonId);
    if (lesson) {
        currentLessonForEdit = lesson;
    }
    
    if (!currentLessonForEdit) {
        showNotification('Không tìm thấy bài học để chỉnh sửa!', 'error');
        return;
    }

    // Điền thông tin vào form
    document.getElementById('lesson-title').value = currentLessonForEdit.title || '';
    document.getElementById('lesson-category').value = currentLessonForEdit.category || '';
    document.getElementById('lesson-level').value = currentLessonForEdit.level || 'N5';
    document.getElementById('lesson-time').value = currentLessonForEdit.estimatedTimeMinutes || 15;
    document.getElementById('lesson-description').value = currentLessonForEdit.description || '';

    showNotification('Chỉnh sửa bài học: ' + currentLessonForEdit.title, 'info');
}

// Xóa bài học
function deleteLesson(lessonId) {
    currentLessonForDeletion = lessonId;
    
    if (confirm('Bạn có chắc chắn muốn xóa bài học này?')) {
        database.ref(`${DB_PATHS.LESSONS}/${lessonId}`).remove()
            .then(() => {
                console.log('Bài học đã được xóa thành công');
                loadLessonsData();
                showNotification('Bài học đã được xóa thành công!', 'success');
                currentLessonForDeletion = null;
            })
            .catch(error => {
                console.error('Lỗi khi xóa bài học:', error);
                showNotification('Lỗi khi xóa bài học: ' + error.message, 'error');
            });
    }
}

// Xem chi tiết danh mục
function viewCategoryDetail(categoryId) {
    const category = currentCategoriesData.find(c => c.id === categoryId);
    
    if (!category) {
        showNotification('Không tìm thấy danh mục!', 'error');
        return;
    }
    
    currentCategoryForEdit = category;
    currentCategoryForDeletion = categoryId;
    
    showNotification(`Danh mục: ${category.name}`, 'info');
}

// Chỉnh sửa danh mục
function editCategory(categoryId) {
    const category = currentCategoriesData.find(c => c.id === categoryId);
    if (category) {
        currentCategoryForEdit = category;
    }
    
    if (!currentCategoryForEdit) {
        showNotification('Không tìm thấy danh mục để chỉnh sửa!', 'error');
        return;
    }

    // Điền thông tin vào form
    document.getElementById('category-name').value = currentCategoryForEdit.name || '';
    document.getElementById('category-icon').value = currentCategoryForEdit.iconUrl || '';
    document.getElementById('category-color').value = currentCategoryForEdit.color || '#8B5CF6';
    document.getElementById('category-order').value = currentCategoryForEdit.order || 1;
    document.getElementById('category-description').value = currentCategoryForEdit.description || '';

    showNotification('Chỉnh sửa danh mục: ' + currentCategoryForEdit.name, 'info');
}

// Xóa danh mục
function deleteCategory(categoryId) {
    currentCategoryForDeletion = categoryId;
    
    if (confirm('Bạn có chắc chắn muốn xóa danh mục này?')) {
        database.ref(`${DB_PATHS.CATEGORIES}/${categoryId}`).remove()
            .then(() => {
                console.log('Danh mục đã được xóa thành công');
                loadCategoriesData();
                showNotification('Danh mục đã được xóa thành công!', 'success');
                currentCategoryForDeletion = null;
            })
            .catch(error => {
                console.error('Lỗi khi xóa danh mục:', error);
                showNotification('Lỗi khi xóa danh mục: ' + error.message, 'error');
            });
    }
}

// Xem chi tiết thành tích
function viewAchievementDetail(achievementId) {
    const achievement = currentAchievementsData.find(a => a.id === achievementId);
    
    if (!achievement) {
        showNotification('Không tìm thấy thành tích!', 'error');
        return;
    }
    
    currentAchievementForEdit = achievement;
    currentAchievementForDeletion = achievementId;
    
    showNotification(`Thành tích: ${achievement.title}`, 'info');
}

// Chỉnh sửa thành tích
function editAchievement(achievementId) {
    const achievement = currentAchievementsData.find(a => a.id === achievementId);
    if (achievement) {
        currentAchievementForEdit = achievement;
    }
    
    if (!currentAchievementForEdit) {
        showNotification('Không tìm thấy thành tích để chỉnh sửa!', 'error');
        return;
    }

    // Điền thông tin vào form
    document.getElementById('achievement-title').value = currentAchievementForEdit.title || '';
    document.getElementById('achievement-icon').value = currentAchievementForEdit.iconUrl || '';
    document.getElementById('achievement-type').value = currentAchievementForEdit.type || 'lesson';
    document.getElementById('achievement-target').value = currentAchievementForEdit.target || 1;
    document.getElementById('achievement-points').value = currentAchievementForEdit.points || 10;
    document.getElementById('achievement-rarity').value = currentAchievementForEdit.rarity || 'common';
    document.getElementById('achievement-color').value = currentAchievementForEdit.color || '#FFD700';
    document.getElementById('achievement-description').value = currentAchievementForEdit.description || '';

    showNotification('Chỉnh sửa thành tích: ' + currentAchievementForEdit.title, 'info');
}

// Xóa thành tích
function deleteAchievement(achievementId) {
    currentAchievementForDeletion = achievementId;
    
    if (confirm('Bạn có chắc chắn muốn xóa thành tích này?')) {
        database.ref(`${DB_PATHS.ACHIEVEMENTS}/${achievementId}`).remove()
            .then(() => {
                console.log('Thành tích đã được xóa thành công');
                loadAchievementsData();
                showNotification('Thành tích đã được xóa thành công!', 'success');
                currentAchievementForDeletion = null;
            })
            .catch(error => {
                console.error('Lỗi khi xóa thành tích:', error);
                showNotification('Lỗi khi xóa thành tích: ' + error.message, 'error');
            });
    }
} 