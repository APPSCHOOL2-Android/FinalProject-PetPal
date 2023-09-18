const functions = require('firebase-functions')
const admin = require('firebase-admin')
const serviceAccount = require("./admin.json")
const request = require('request-promise')

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
});


const kakaoRequestMeUrl = 'https://kapi.kakao.com/v2/user/me'

function requestMe(kakaoAccessToken) {
    console.log('Requesting user profile from Kakao API server.')
    return request({
        method: 'GET',
        headers: { 'Authorization': 'Bearer ' + kakaoAccessToken },
        url: kakaoRequestMeUrl,
    })
}

async function updateOrCreateUser(userId, email, displayName, photoURL) {
    console.log('updating or creating a firebase user');
    const updateParams = {
        provider: 'KAKAO',
        displayName: displayName,
        email: email,
    };
    if (displayName) {
        updateParams['displayName'] = displayName;
    } else {
        updateParams['displayName'] = email;
    }
    if (photoURL) {
        updateParams['photoURL'] = photoURL;
    }
    console.log(updateParams);
    try {
        return await admin.auth().updateUser(userId, updateParams)
    } catch (error) {
        if (error.code === 'auth/user-not-found') {
            updateParams['uid'] = userId
            if (email) {
                updateParams['email'] = email
            }
            return admin.auth().createUser(updateParams)
        }
        throw error
    }
}

async function createFirebaseToken(kakaoAccessToken) {
    const response = await requestMe(kakaoAccessToken)
    const body = JSON.parse(response)
    console.log(body.properties)
    const userId = `kakao:${body.id}`
    if (!userId) {
        throw new functions.https.HttpsError('invalid-argument', 'Not response: Failed get userId')
    }
    let nickname = null
    let profileImage = null
    let email = null
    if (body.properties) {
        nickname = body.properties.nickname
        profileImage = body.properties.profile_image
    }
    if (body.kakao_account) {
        email = body.kakao_account.email
    }
    const userRecord = await updateOrCreateUser(userId, email, nickname,
        profileImage)
    const userId_2 = userRecord.uid
    console.log(`creating a custom firebase token based on uid ${userId_2}`)
    return await admin.auth().createCustomToken(userId_2, { provider: 'KAKAO' })
}

exports.kakaoCustomAuth = functions.region('asia-northeast3').https
    .onCall(async (data) => {
        const token = data.token

        if (!(typeof token === 'string') || token.length === 0) {
            throw new functions.https.HttpsError('invalid-argument', 'The function must be called with ' +
                'one arguments "data" containing the token to add.');
        }

        console.log(`Verifying Kakao token: ${token}`)

        const firebaseToken = await createFirebaseToken(token)
        console.log(`Returning firebase token to user: ${firebaseToken}`)
        return { "custom_token": firebaseToken }

    })