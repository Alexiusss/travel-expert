const Footer = () => {
    return (
        <footer className="text-center text-lg-start bg-light text-muted">
            <div className="text-center p-4" >
                <div className="container">
                    © {new Date().getFullYear()} Copyright Text
                    <a href="#!">
                         GitHub
                    </a>
                </div>
            </div>
        </footer>
    );
}

export default Footer;