import {useSelector} from 'react-redux'

export function useAuth() {
    const {email, token, id, authorities} = useSelector(state => state.user)

    return {
        isAuth: !!email,
        isAdmin: authorities.includes("ADMIN"),
        isModerator: authorities.includes("MODERATOR"),
        email,
        token,
        authUserId: id,
    };
}