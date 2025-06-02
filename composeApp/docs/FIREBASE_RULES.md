# Firebase Security Rules

Dưới đây là các quy tắc bảo mật cho Firebase Realtime Database trong ứng dụng học tiếng Nhật. Copy các quy tắc này vào Firebase Console.

## Rules

```json
{
  "rules": {
    "app_data": {
      "users": {
        "$uid": {
          ".read": "$uid === auth.uid",
          ".write": "$uid === auth.uid",
          
          "profile": {
            ".validate": "newData.hasChildren(['name', 'age', 'currentLevel', 'targetLevel'])",
            "name": {
              ".validate": "newData.isString() && newData.val().length > 0 && newData.val().length < 100"
            },
            "age": {
              ".validate": "newData.isNumber() && newData.val() > 0 && newData.val() < 120"
            },
            "currentLevel": {
              ".validate": "newData.isString() && newData.val().matches(/^(Mới bắt đầu|N5|N4|N3|N2|N1)$/)"
            },
            "targetLevel": {
              ".validate": "newData.isString() && newData.val().matches(/^(Mới bắt đầu|N5|N4|N3|N2|N1)$/)"
            },
            "avatarUrl": {
              ".validate": "newData.isString() || newData.val() === null"
            },
            "registrationDate": {
              ".validate": "newData.isNumber() && newData.val() <= now"
            }
          },
          
          "progress": {
            ".validate": "newData.hasChildren(['streak', 'wordsLearned', 'lessonsCompleted'])",
            "streak": {
              ".validate": "newData.isNumber() && newData.val() >= 0"
            },
            "wordsLearned": {
              ".validate": "newData.isNumber() && newData.val() >= 0"
            },
            "lessonsCompleted": {
              ".validate": "newData.isNumber() && newData.val() >= 0"
            },
            "daysActive": {
              ".validate": "newData.isNumber() && newData.val() >= 0"
            },
            "lastActiveDate": {
              ".validate": "newData.isNumber()"
            }
          },
          
          "settings": {
            "studyTimeMinutes": {
              ".validate": "newData.isNumber() && newData.val() >= 15 && newData.val() <= 120"
            }
          },
          
          "learning": {
            ".write": "$uid === auth.uid",
            ".read": "$uid === auth.uid"
          }
        }
      },
      
      "vocabulary": {
        ".read": "auth !== null",
        ".write": "auth !== null && root.child('app_data/users').child(auth.uid).child('admin').exists()",
        
        "$wordId": {
          ".validate": "newData.hasChildren(['japanese', 'reading', 'vietnamese', 'level'])",
          "japanese": {
            ".validate": "newData.isString() && newData.val().length > 0"
          },
          "reading": {
            ".validate": "newData.isString() && newData.val().length > 0"
          },
          "vietnamese": {
            ".validate": "newData.isString() && newData.val().length > 0"
          },
          "level": {
            ".validate": "newData.isString() && newData.val().matches(/^(N5|N4|N3|N2|N1)$/)"
          },
          "categories": {
            ".validate": "newData.hasChildren() || newData.val() === null"
          },
          "exampleSentences": {
            ".validate": "newData.hasChildren() || newData.val() === null",
            "$sentenceId": {
              ".validate": "newData.hasChildren(['japanese', 'vietnamese'])",
              "japanese": {
                ".validate": "newData.isString() && newData.val().length > 0"
              },
              "vietnamese": {
                ".validate": "newData.isString() && newData.val().length > 0"
              }
            }
          }
        }
      },
      
      "lessons": {
        ".read": "auth !== null",
        ".write": "auth !== null && root.child('app_data/users').child(auth.uid).child('admin').exists()",
        
        "$lessonId": {
          ".validate": "newData.hasChildren(['title', 'description', 'level', 'category', 'order'])",
          "title": {
            ".validate": "newData.isString() && newData.val().length > 0"
          },
          "description": {
            ".validate": "newData.isString() && newData.val().length > 0"
          },
          "level": {
            ".validate": "newData.isString() && newData.val().matches(/^(N5|N4|N3|N2|N1)$/)"
          },
          "category": {
            ".validate": "newData.isString() && newData.val().length > 0"
          },
          "order": {
            ".validate": "newData.isNumber() && newData.val() >= 0"
          },
          "vocabularyIds": {
            ".validate": "newData.hasChildren() || newData.val() === null"
          },
          "grammarPoints": {
            ".validate": "newData.hasChildren() || newData.val() === null",
            "$grammarId": {
              ".validate": "newData.hasChildren(['rule', 'explanation'])",
              "rule": {
                ".validate": "newData.isString() && newData.val().length > 0"
              },
              "explanation": {
                ".validate": "newData.isString() && newData.val().length > 0"
              },
              "examples": {
                ".validate": "newData.hasChildren() || newData.val() === null"
              }
            }
          }
        }
      },
      
      "categories": {
        ".read": "auth !== null",
        ".write": "auth !== null && root.child('app_data/users').child(auth.uid).child('admin').exists()",
        
        "$categoryId": {
          ".validate": "newData.hasChildren(['name', 'description'])",
          "name": {
            ".validate": "newData.isString() && newData.val().length > 0"
          },
          "description": {
            ".validate": "newData.isString() && newData.val().length > 0"
          },
          "iconUrl": {
            ".validate": "newData.isString() || newData.val() === null"
          }
        }
      }
    }
  }
}
```

## Giải thích các quy tắc

### Users

- Mỗi người dùng chỉ có thể đọc và ghi dữ liệu của chính mình
- Dữ liệu profile phải bao gồm các trường bắt buộc và tuân thủ các ràng buộc về định dạng
- Trình độ tiếng Nhật phải nằm trong danh sách cho phép
- Tuổi phải là số dương và hợp lý

### Vocabulary, Lessons, Categories

- Tất cả người dùng đã đăng nhập đều có thể đọc
- Chỉ quản trị viên mới có thể thêm/sửa/xóa từ vựng, bài học và danh mục
- Quản trị viên được xác định khi có trường `admin` trong dữ liệu người dùng

### Learning Progress

- Người dùng có toàn quyền đọc/ghi dữ liệu học tập của họ
- Tiến độ từ vựng và bài học được lưu trữ trong node `learning` của mỗi người dùng

## Cách áp dụng Rules

1. Truy cập [Firebase Console](https://console.firebase.google.com/)
2. Chọn dự án của bạn
3. Chọn "Realtime Database" từ sidebar bên trái
4. Chọn tab "Rules"
5. Copy nội dung rules ở trên và dán vào editor
6. Nhấn "Publish" 