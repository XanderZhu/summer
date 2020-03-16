//
//  BasketConrtoller.swift
//  iosApp
//
//  Created by adev on 14.03.2020.
//

import UIKit
import shared

extension BasketController: BasketViewMethods {
    
}

extension BasketController: BasketRouter {
    
}

class BasketController: BaseController, BasketViewState {
    
    @IBOutlet weak var basketLabel: UILabel!
    
    var items: [Basket.Item] = [] {
        didSet {
            basketLabel.text = items
                .map { item in "\(item.framework.name)=\(item.quantity)" }
                .joined(separator: "\n")
        }
    }
    
    private var presenter: BasketPresenter! {
        didSet { setPresenter(presenter) }
    }
    
    override func viewDidLoad() {
        presenter = BasketPresenter()
        super.viewDidLoad()
    }
    
}