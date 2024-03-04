
const signUpButton = document.getElementById('signUpButton');

signUpButton.addEventListener('click', async (event) => {
    event.preventDefault();
    await signup();
});

let params = new URLSearchParams(window.location.search);
let accessToken = params.get("accessToken");

saveAccessTokenToLocalStorage(accessToken);

function signup(){
    const name = document.getElementById('username').value;
    const data = {
        name: name
    };

    fetch('/api/oauth2/signup', {
        method: 'POST',
        body: JSON.stringify(data),
        headers: {
            'Content-Type': 'application/json; charset=UTF-8',
            Accept: 'application/json'
        }
    }).then(response => response.json())
        .then(data => {
            if(data.status === 200){
                // window.location.href = "/chat";
                //saveAccessTokenToLocalStorage(data.accessToken);
                //saveRefreshTokenToLocalStorage(data.refreshToken);
                //navigateTo("/chat");
            }else{
                alert(data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

