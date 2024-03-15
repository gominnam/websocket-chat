
const signUpButton = document.getElementById('signUpButton');

signUpButton.addEventListener('click', async (event) => {
    event.preventDefault();
    await signup();
});

let params = new URLSearchParams(window.location.search);
let accessToken = params.get("accessToken");

async function signup(){
    const name = document.getElementById('username').value;
    const data = {
        name: name
    };
    let headers = {
        'Content-Type': 'application/json'
    };
    let authToken= getAuthToken();
    if(authToken){
        headers[authToken.key] = authToken.value;
    }

    showLoadingSpinner();
    try {
        const response = await fetch('/api/oauth2/signup', {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(data),
        });

        if (response.ok) {
            await navigateTo('/chat');
        } else {
            // 오류 처리
            response.json().then(data => {
                // Assuming the error message is in the 'message' field of the response
                const errorMessage = data.message || "An unknown error occurred"; // Fallback message
                window.alert(errorMessage);
            }).catch(() => {
                // This catch handles any errors that occur during the parsing process
                window.alert("An error occurred while processing the error message.");
                throw new Error('Network response was not ok and error message could not be retrieved');
            });
        }
    } catch (error) {
        console.error('요청 오류', error);
    } finally {
        // 화면 잠금 해제
        hideLoadingSpinner();
    }
}
