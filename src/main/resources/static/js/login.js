const signUpButton = document.getElementById('signUp');
const signInButton = document.getElementById('signIn');
const userSignInButton = document.getElementById('userSignIn');
const container = document.getElementById('container');

signInButton.addEventListener('click', () => {
    container.classList.remove("right-panel-active");
});

signUpButton.addEventListener('click', () => {
    container.classList.add("right-panel-active");
});

userSignInButton.addEventListener('click', () => {
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    if(!loginValidation(email, password)) return;

    const data = {
        email: email,
        password: password,
        name: ""
    };

    fetch('/api/user/login', {
        method: 'POST',
        body: JSON.stringify(data),
        headers: {
            'Content-Type': 'application/json; charset=UTF-8',
            Accept: 'application/json'
        }
    }).then(response => response.json())
        .then(data => {
            console.log("data : " + JSON.stringify(data));
            if(data.status === 200){
                window.location.href = "/chat";
            }else{
                alert("Login failed");
            }
        })
        .catch(error => {
        console.error('Error:', error);
    });
});

function loginValidation(email, password){
    if(email == null || email === ""){
        alert("Email is required");
        return false;
    }
    if(password == null || password === ""){
        alert("Password is required");
        return false;
    }
    return true;
}

