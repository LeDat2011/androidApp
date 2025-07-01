// DOM Elements
const connectionStatus = document.getElementById('connection-status');
const userStatus = document.getElementById('user-status');
const navVocabulary = document.getElementById('nav-vocabulary');
const navQuiz = document.getElementById('nav-quiz');
const navUsers = document.getElementById('nav-users');
const vocabularySection = document.getElementById('vocabulary-section');
const quizSection = document.getElementById('quiz-section');
const usersSection = document.getElementById('users-section');
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
let isConnected = false;
let isAuthenticated = false;
let currentUserForDeletion = null;
let currentUserStatus = 'active'; // Trạng thái người dùng hiện tại

// Khởi tạo ứng dụng
document.addEventListener('DOMContentLoaded', () => {
    // Kiểm tra các phần tử DOM có tồn tại
    if (!connectionStatus || !userStatus) {
        console.error('Không tìm thấy phần tử DOM cần thiết!');
        return;
    }
    
    // Kiểm tra kết nối Firebase
    checkFirebaseConnection();
    
    // Kiểm tra trạng thái đăng nhập
    checkAuthStatus();
    
    // Thiết lập sự kiện chuyển tab
    setupNavigation();
    
    // Thiết lập sự kiện form
    setupForms();
    
    // Thiết lập bộ lọc
    setupFilters();
    
    // Tải dữ liệu ban đầu
    loadVocabularyData();
    loadQuizData();
    loadUsersData();
    
    // Thiết lập sự kiện xóa người dùng
    setupUserDeletion();
});

// Kiểm tra kết nối Firebase
function checkFirebaseConnection() {
    const connectedRef = database.ref('.info/connected');
    
    connectedRef.on('value', (snap) => {
        isConnected = snap.val() === true;
        
        if (isConnected) {
            connectionStatus.textContent = 'Đã kết nối đến Firebase';
            connectionStatus.className = 'connected';
        } else {
            connectionStatus.textContent = 'Mất kết nối đến Firebase';
            connectionStatus.className = 'disconnected';
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
    navVocabulary.addEventListener('click', (e) => {
        e.preventDefault();
        showSection('vocabulary');
    });
    
    navQuiz.addEventListener('click', (e) => {
        e.preventDefault();
        showSection('quiz');
    });
    
    navUsers.addEventListener('click', (e) => {
        e.preventDefault();
        showSection('users');
        // Load dữ liệu người dùng khi chuyển đến tab users
        if (isAuthenticated) {
            loadUsersData();
        }
    });
}

// Hiển thị phần tương ứng
function showSection(section) {
    // Cập nhật trạng thái active của menu
    navVocabulary.classList.toggle('active', section === 'vocabulary');
    navQuiz.classList.toggle('active', section === 'quiz');
    navUsers.classList.toggle('active', section === 'users');
    
    // Hiển thị phần tương ứng
    vocabularySection.style.display = section === 'vocabulary' ? 'block' : 'none';
    quizSection.style.display = section === 'quiz' ? 'block' : 'none';
    usersSection.style.display = section === 'users' ? 'block' : 'none';
    
    // Load dữ liệu tương ứng
    if (section === 'vocabulary' && isAuthenticated) {
        loadVocabularyData();
    } else if (section === 'quiz' && isAuthenticated) {
        loadQuizData();
    } else if (section === 'users' && isAuthenticated) {
        loadUsersData();
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
        
        // Tạo đối tượng từ vựng
        const vocabularyItem = {
            japanese: japaneseWord,
            reading: reading,
            vietnamese: vietnameseMeaning,
            example: exampleJapanese && exampleVietnamese ? 
                `${exampleJapanese} - ${exampleVietnamese}` : ''
        };
        
        // Thêm vào Firebase
        addVocabulary(category, level, vocabularyItem);
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
        const categoryMatch = categoryFilter === 'all' || item.category === categoryFilter;
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
    vocabularyList.innerHTML = '<tr><td colspan="7" class="text-center">Đang tải dữ liệu...</td></tr>';
    
    database.ref(DB_PATHS.VOCABULARY).once('value')
        .then(snapshot => {
            currentVocabularyData = [];
            
            if (snapshot.exists()) {
                snapshot.forEach(categorySnapshot => {
                    const category = categorySnapshot.key;
                    
                    categorySnapshot.forEach(levelSnapshot => {
                        const level = levelSnapshot.key;
                        
                        levelSnapshot.forEach(itemSnapshot => {
                            const id = itemSnapshot.key;
                            const data = itemSnapshot.val();
                            
                            currentVocabularyData.push({
                                id,
                                category,
                                level,
                                ...data
                            });
                        });
                    });
                });
            }
            
            renderVocabularyList(currentVocabularyData);
        })
        .catch(error => {
            console.error('Lỗi khi tải dữ liệu từ vựng:', error);
            vocabularyList.innerHTML = '<tr><td colspan="7" class="text-center text-danger">Lỗi khi tải dữ liệu</td></tr>';
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
    if (data.length === 0) {
        vocabularyList.innerHTML = '<tr><td colspan="7" class="text-center">Không có dữ liệu</td></tr>';
        return;
    }
    
    vocabularyList.innerHTML = data.map(item => `
        <tr>
            <td>${item.id}</td>
            <td class="japanese-text">${item.japanese}</td>
            <td class="japanese-text">${item.reading}</td>
            <td>${item.vietnamese}</td>
            <td>${item.category}</td>
            <td>${item.level}</td>
            <td>
                <div class="d-flex justify-content-center gap-1">
                    <button class="btn btn-sm btn-outline-info btn-action" onclick="viewVocabularyDetail('${item.id}', '${item.category}', '${item.level}')">
                        <i class="fas fa-eye"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger btn-action" onclick="deleteVocabulary('${item.id}', '${item.category}', '${item.level}')">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
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
                        <button class="btn btn-sm btn-outline-info btn-action" onclick="viewQuizDetail('${item.id}', '${item.category}', '${item.level}', '${item.quizId}')">
                            <i class="fas fa-eye"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-danger btn-action" onclick="deleteQuiz('${item.id}', '${item.category}', '${item.level}', '${item.quizId}')">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;
    }).join('');
}

// Thêm từ vựng mới
function addVocabulary(category, level, vocabularyItem) {
    // Lấy danh sách từ vựng hiện có để tìm số lớn nhất
    database.ref(`${DB_PATHS.VOCABULARY}/${category}/${level}`).once('value')
        .then(snapshot => {
            let maxNumber = 0;
            
            // Tìm số lớn nhất trong các ID hiện có
            if (snapshot.exists()) {
                snapshot.forEach(childSnapshot => {
                    const key = childSnapshot.key;
                    if (key.startsWith('vocab')) {
                        const numStr = key.replace('vocab', '');
                        const num = parseInt(numStr, 10);
                        if (!isNaN(num) && num > maxNumber) {
                            maxNumber = num;
                        }
                    }
                });
            }
            
            // Tạo ID mới với số lớn hơn 1
            const newId = `vocab${maxNumber + 1}`;
            
            // Thêm dữ liệu với ID được chỉ định
            return database.ref(`${DB_PATHS.VOCABULARY}/${category}/${level}/${newId}`).set(vocabularyItem);
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

// Xem chi tiết từ vựng
function viewVocabularyDetail(id, category, level) {
    database.ref(`${DB_PATHS.VOCABULARY}/${category}/${level}/${id}`).once('value')
        .then(snapshot => {
            if (snapshot.exists()) {
                const data = snapshot.val();
                
                const detailContent = document.getElementById('vocabulary-detail-content');
                detailContent.innerHTML = `
                    <div class="detail-item">
                        <div class="detail-label">ID:</div>
                        <div class="detail-value">${id}</div>
                    </div>
                    <div class="detail-item">
                        <div class="detail-label">Từ tiếng Nhật:</div>
                        <div class="detail-value japanese-text">${data.japanese}</div>
                    </div>
                    <div class="detail-item">
                        <div class="detail-label">Cách đọc:</div>
                        <div class="detail-value japanese-text">${data.reading}</div>
                    </div>
                    <div class="detail-item">
                        <div class="detail-label">Nghĩa tiếng Việt:</div>
                        <div class="detail-value">${data.vietnamese}</div>
                    </div>
                    <div class="detail-item">
                        <div class="detail-label">Danh mục:</div>
                        <div class="detail-value">${category}</div>
                    </div>
                    <div class="detail-item">
                        <div class="detail-label">Cấp độ:</div>
                        <div class="detail-value">${level}</div>
                    </div>
                `;
                
                if (data.example) {
                    const parts = data.example.split(' - ');
                    const exampleJapanese = parts[0] || '';
                    const exampleVietnamese = parts[1] || '';
                    
                    detailContent.innerHTML += `
                        <div class="detail-item">
                            <div class="detail-label">Ví dụ tiếng Nhật:</div>
                            <div class="detail-value japanese-text">${exampleJapanese}</div>
                        </div>
                        <div class="detail-item">
                            <div class="detail-label">Nghĩa ví dụ:</div>
                            <div class="detail-value">${exampleVietnamese}</div>
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
                
                const detailContent = document.getElementById('quiz-detail-content');
                detailContent.innerHTML = `
                    <div class="detail-item">
                        <div class="detail-label">ID:</div>
                        <div class="detail-value">${id}</div>
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
                
                detailContent.innerHTML += `
                    <div class="detail-item">
                        <div class="detail-label">Danh mục:</div>
                        <div class="detail-value">${category}</div>
                    </div>
                    <div class="detail-item">
                        <div class="detail-label">Cấp độ:</div>
                        <div class="detail-value">${level}</div>
                    </div>
                `;
                
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
function deleteVocabulary(id, category, level) {
    if (confirm('Bạn có chắc chắn muốn xóa từ vựng này?')) {
        database.ref(`${DB_PATHS.VOCABULARY}/${category}/${level}/${id}`).remove()
            .then(() => {
                alert('Xóa từ vựng thành công!');
                loadVocabularyData();
            })
            .catch(error => {
                console.error('Lỗi khi xóa từ vựng:', error);
                alert('Lỗi khi xóa từ vựng. Vui lòng thử lại!');
            });
    }
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
    usersList.innerHTML = `<tr><td colspan="7" class="text-center">Đang tải dữ liệu người dùng...</td></tr>`;
    
    // Lấy danh sách người dùng từ Firebase
    const usersRef = database.ref(DB_PATHS.USERS);
    usersRef.once('value')
        .then((snapshot) => {
            const usersData = [];
            snapshot.forEach((userSnapshot) => {
                    const userId = userSnapshot.key;
                const profile = userSnapshot.child('profile').val() || {};
                const progress = userSnapshot.child('progress').val() || {};
                const settings = userSnapshot.child('settings').val() || {};
                    
                // Lấy thông tin từ profile
                const name = profile.name || 'Không có tên';
                const email = profile.email || 'Không có email'; // Add email field
                const age = profile.age || 0;
                const currentLevel = profile.currentLevel || 'N5';
                const targetLevel = profile.targetLevel || 'N5';
                const avatarUrl = profile.avatarUrl || '';
                const registrationDate = profile.registrationDate || 0;
                
                // Lấy thông tin từ progress
                const streak = progress.streak || 0;
                const wordsLearned = progress.wordsLearned || 0;
                const lessonsCompleted = progress.lessonsCompleted || 0;
                const daysActive = progress.daysActive || 0;
                const lastActiveDate = progress.lastActiveDate || 0;
                
                // Lấy thông tin từ settings
                const studyTimeMinutes = settings.studyTimeMinutes || 30;
                const status = settings.status || 'active';
                
                console.log("Loaded user:", { id: userId, name, email }); // Thêm log để debug
                
                // Thêm người dùng vào danh sách
                usersData.push({
                        id: userId,
                    name,
                    email, // Include email in userData
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
                    status
                    });
                });
            
            // Hiển thị danh sách người dùng
            currentUsersData = usersData;
            console.log("Total users loaded:", usersData.length); // Log số lượng user đã load
            renderUsersList(usersData);
        })
        .catch((error) => {
            console.error('Lỗi khi tải dữ liệu người dùng:', error);
            usersList.innerHTML = `<tr><td colspan="7" class="text-danger text-center">Lỗi khi tải dữ liệu người dùng</td></tr>`;
        });
}

// Hiển thị danh sách người dùng
function renderUsersList(data) {
    if (!data || data.length === 0) {
        usersList.innerHTML = `<tr><td colspan="7" class="text-center">Không có dữ liệu người dùng</td></tr>`;
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
        
        html += `
            <tr>
                <td class="user-id">${user.id ? user.id.substring(0, 10) : 'N/A'}...</td>
                <td>${user.email || 'N/A'}</td>
                <td>${user.name || 'N/A'}</td>
                <td>${registrationDateStr}</td>
                <td>${user.currentLevel || 'N/A'}</td>
                <td class="${statusClass}">${statusName}</td>
                <td>
                    <button class="btn btn-sm btn-primary view-user" data-id="${user.id}">
                            <i class="fas fa-eye"></i>
                        </button>
                </td>
            </tr>
        `;
    });
    
    usersList.innerHTML = html;
    
    // Thêm sự kiện cho nút xem chi tiết
    document.querySelectorAll('.view-user').forEach((btn) => {
        btn.addEventListener('click', function() {
            const userId = this.getAttribute('data-id');
            viewUserDetail(userId);
        });
    });
}

// Xem chi tiết người dùng
function viewUserDetail(userId) {
    // Tìm dữ liệu người dùng
    const user = currentUsersData.find((u) => u.id === userId);
    
    if (!user) {
        return;
    }
    
    // Lưu trữ thông tin người dùng đang xem
    currentUserForDeletion = userId;
    currentUserStatus = user.status || 'active';
                
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